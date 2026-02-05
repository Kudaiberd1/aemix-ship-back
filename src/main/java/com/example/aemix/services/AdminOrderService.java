package com.example.aemix.services;

import com.example.aemix.dto.requests.BulkReadyRequest;
import com.example.aemix.dto.requests.ImportOrdersRequest;
import com.example.aemix.dto.responses.BulkOperationResponse;
import com.example.aemix.dto.responses.ImportOrdersResponse;
import com.example.aemix.dto.responses.OrderResponse;
import com.example.aemix.entities.City;
import com.example.aemix.entities.Order;
import com.example.aemix.entities.ScanLogs;
import com.example.aemix.entities.User;
import com.example.aemix.entities.enums.OrderSort;
import com.example.aemix.entities.enums.Status;
import com.example.aemix.exceptions.BusinessValidationException;
import com.example.aemix.exceptions.ResourceNotFoundException;
import com.example.aemix.mappers.OrderMapper;
import com.example.aemix.repositories.CityRepository;
import com.example.aemix.repositories.OrderRepository;
import com.example.aemix.repositories.ScanLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ScanLogsRepository scanLogsRepository;
    private final CityRepository cityRepository;

    public Page<OrderResponse> getOrders(
            String trackCode,
            Status status,
            Long cityId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size,
            OrderSort orderSort
    ) {
        Sort sort;
        if (orderSort == null || orderSort == OrderSort.CREATED_DESC) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt"); // сначала новые
        } else if (orderSort == OrderSort.CREATED_ASC) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");  // сначала старые
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.findOrders(trackCode, status, cityId, fromDate, toDate, pageable)
                .map(orderMapper::toDto);

    }

    @Transactional
    public OrderResponse scanArrived(String trackCode, User user) {
        Order order = orderRepository.findByTrackCode(trackCode)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с трек-кодом " + trackCode + " не найден"));

        if (order.getStatus() != Status.INTERNATIONAL_SHIPPING) {
            throw new BusinessValidationException(
                    "Заказ должен иметь статус INTERNATIONAL_SHIPPING. Текущий статус: " + order.getStatus()
            );
        }

        Status oldStatus = order.getStatus();
        order.setStatus(Status.ARRIVED);
        orderRepository.save(order);

        ScanLogs scanLog = ScanLogs.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(Status.ARRIVED)
                .user(user)
                .build();
        scanLogsRepository.save(scanLog);

        log.info("Заказ {} отсканирован: {} -> {} пользователем {}", trackCode, oldStatus, Status.ARRIVED, user.getEmailOrTelegramId());
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponse scanReady(String trackCode, User user) {
        Order order = orderRepository.findByTrackCode(trackCode)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с трек-кодом " + trackCode + " не найден"));

        if (order.getStatus() != Status.ARRIVED) {
            throw new BusinessValidationException(
                    "Заказ должен иметь статус ARRIVED. Текущий статус: " + order.getStatus()
            );
        }

        Status oldStatus = order.getStatus();
        order.setStatus(Status.READY);
        orderRepository.save(order);

        ScanLogs scanLog = ScanLogs.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(Status.READY)
                .user(user)
                .build();
        scanLogsRepository.save(scanLog);

        log.info("Заказ {} помечен как готов: {} -> {} пользователем {}", trackCode, oldStatus, Status.READY, user.getEmailOrTelegramId());
        return orderMapper.toDto(order);
    }

    @Transactional
    public BulkOperationResponse bulkReady(BulkReadyRequest request, User user) {
        List<String> trackCodes = request.getTrackCodes();
        List<Order> orders = orderRepository.findByTrackCodeInAndStatus(trackCodes, Status.ARRIVED);

        int updated = 0;
        List<String> errors = new ArrayList<>();
        List<String> processedTrackCodes = new ArrayList<>();

        for (Order order : orders) {
            try {
                Status oldStatus = order.getStatus();
                order.setStatus(Status.READY);
                orderRepository.save(order);

                ScanLogs scanLog = ScanLogs.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(Status.READY)
                        .user(user)
                        .build();
                scanLogsRepository.save(scanLog);

                updated++;
                processedTrackCodes.add(order.getTrackCode());
            } catch (Exception e) {
                log.error("Ошибка при обновлении заказа {}: {}", order.getTrackCode(), e.getMessage());
                errors.add(order.getTrackCode());
            }
        }

        for (String trackCode : trackCodes) {
            if (!processedTrackCodes.contains(trackCode) && !errors.contains(trackCode)) {
                errors.add(trackCode);
            }
        }

        log.info("Массовое обновление заказов: обновлено {}, ошибок {}", updated, errors.size());
        return BulkOperationResponse.builder()
                .total(trackCodes.size())
                .updated(updated)
                .skipped(trackCodes.size() - updated)
                .errors(errors)
                .build();
    }

    @Transactional
    public ImportOrdersResponse importOrders(ImportOrdersRequest request, User user) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("Город с ID " + request.getCityId() + " не найден"));

        int total = request.getOrders().size();
        int created = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        for (ImportOrdersRequest.OrderImportItem item : request.getOrders()) {
            try {
                String trackCode = item.getTrackCode();
                if (trackCode == null || trackCode.trim().isEmpty()) {
                    skipped++;
                    errors.add("Пустой трек-код");
                    continue;
                }

                if (orderRepository.findByTrackCode(trackCode.trim()).isPresent()) {
                    skipped++;
                    errors.add(trackCode + " - заказ уже существует");
                    continue;
                }

                Order order = Order.builder()
                        .trackCode(trackCode.trim())
                        .status(Status.INTERNATIONAL_SHIPPING)
                        .city(city)
                        .build();
                order = orderRepository.save(order);
                orderRepository.flush();
                
                order = orderRepository.findByTrackCode(trackCode.trim())
                        .orElseThrow(() -> new RuntimeException("Заказ не найден после сохранения"));

                ScanLogs scanLog = ScanLogs.builder()
                        .order(order)
                        .oldStatus(Status.UNKNOWN)
                        .newStatus(Status.INTERNATIONAL_SHIPPING)
                        .user(user)
                        .build();
                scanLogsRepository.save(scanLog);

                created++;
            } catch (Exception e) {
                log.error("Ошибка при импорте заказа {}: {}", item.getTrackCode(), e.getMessage());
                skipped++;
                errors.add(item.getTrackCode() + " - " + e.getMessage());
            }
        }

        log.info("Импорт заказов: всего {}, создано {}, пропущено {}", total, created, skipped);
        return ImportOrdersResponse.builder()
                .total(total)
                .created(created)
                .skipped(skipped)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }
}
