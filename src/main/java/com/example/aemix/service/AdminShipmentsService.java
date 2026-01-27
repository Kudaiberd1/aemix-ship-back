package com.example.aemix.service;

import com.example.aemix.dto.responses.UserResponse;
import com.example.aemix.entity.ShipmentEvents;
import com.example.aemix.entity.Shipments;
import com.example.aemix.entity.User;
import com.example.aemix.entity.enums.Role;
import com.example.aemix.entity.enums.Stage;
import com.example.aemix.exception.FileBadRequestException;
import com.example.aemix.mapper.UserMapper;
import com.example.aemix.repository.ShipmentEventsRepository;
import com.example.aemix.repository.ShipmentsRepository;
import com.example.aemix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminShipmentsService {

    private final ShipmentsRepository shipmentsRepository;
    private final ShipmentEventsRepository shipmentEventsRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void uploadShipments(MultipartFile file) {
        validateExcel(file);

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<Shipments> result = new ArrayList<>();
            List<ShipmentEvents> events = new ArrayList<>();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String trackCode = getString(row.getCell(0));

                if (isBlank(trackCode)) {
                    continue;
                }

                Shipments shipments = Shipments.builder()
                                .trackCode(trackCode)
                                .curentStage(Stage.IN_TRANSIT).build();

                result.add(shipments);
            }

            List<Shipments> results = shipmentsRepository.saveAll(result);

            for(int i=0; i<results.size(); i++){
                ShipmentEvents shipmentEvent = ShipmentEvents.builder()
                                .shipmentId(results.get(i).getId())
                                .oldStage(Stage.CHINA_WAREHOUSE)
                                .currentStage(Stage.IN_TRANSIT)
                                .build();
                events.add(shipmentEvent);
            }

            shipmentEventsRepository.saveAll(events);

        }catch (IOException e){
            throw new FileBadRequestException("Invalid Excel file");
        }
    }

    private void validateExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx is supported");
        }
    }

    private String getString(Cell cell) {
        if (cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public void updateShipment(String trackCode, Stage stage) {
        Shipments shipment = shipmentsRepository.findByTrackCode(trackCode).orElseThrow(() -> new IllegalArgumentException("Invalid track code"));
        Stage oldStage = shipment.getCurentStage();
        shipment.setCurentStage(stage);
        shipmentsRepository.save(shipment);

        ShipmentEvents event = ShipmentEvents.builder()
                .shipmentId(shipment.getId())
                .oldStage(oldStage)
                .currentStage(stage)
                .build();
        shipmentEventsRepository.save(event);
    }

    public void deleteShipment(String trackCode) {
        Shipments shipment = shipmentsRepository.findByTrackCode(trackCode).orElseThrow(() -> new IllegalArgumentException("Invalid track code"));
        shipmentsRepository.delete(shipment);

        ShipmentEvents event = ShipmentEvents.builder()
                .shipmentId(shipment.getId())
                .oldStage(shipment.getCurentStage())
                .currentStage(Stage.DELETED)
                .build();
        shipmentEventsRepository.save(event);
    }

    public void createShipments(String trackCode) {
        Shipments shipments = Shipments.builder()
                .trackCode(trackCode)
                .curentStage(Stage.IN_TRANSIT).build();
        shipmentsRepository.save(shipments);

        ShipmentEvents shipmentEvent = ShipmentEvents.builder()
                .shipmentId(shipments.getId())
                .oldStage(Stage.CHINA_WAREHOUSE)
                .currentStage(Stage.IN_TRANSIT)
                .build();
        shipmentEventsRepository.save(shipmentEvent);

    }

    public List<UserResponse> getUsers(){
        List<User> users = userRepository.findAll();

        return users.stream().map(userMapper::toDto).toList();
    }

    public void changeUserRole(String userId, String role) {
        User user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        role = role.replace("\"", "");
        user.setRole(role.equalsIgnoreCase("Admin") ? Role.ROLE_ADMIN : Role.ROLE_USER);
        userRepository.save(user);
    }
}
