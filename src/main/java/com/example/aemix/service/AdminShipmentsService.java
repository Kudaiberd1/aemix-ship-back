package com.example.aemix.service;

import com.example.aemix.dto.requests.UpdateShipmentRequest;
import com.example.aemix.entity.ShipmentEvents;
import com.example.aemix.entity.Shipments;
import com.example.aemix.entity.enums.Stage;
import com.example.aemix.exception.FileBadRequestException;
import com.example.aemix.repository.ShipmentEventsRepository;
import com.example.aemix.repository.ShipmentsRepository;
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
                                .stage(Stage.IN_TRANSIT)
                                .comment(results.get(i).getTrackCode()+" product is currently in transit.").build();

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

    public void updateShipment(String trackCode, UpdateShipmentRequest request) {
        Shipments shipment = shipmentsRepository.findByTrackCode(trackCode).orElseThrow(() -> new IllegalArgumentException("Invalid track code"));
        shipment.setCurentStage(request.getStage());
        shipmentsRepository.save(shipment);

        ShipmentEvents event = ShipmentEvents.builder()
                .shipmentId(shipment.getId())
                .stage(request.getStage())
                .comment(request.getComment())
                .build();
        shipmentEventsRepository.save(event);
    }

    public void deleteShipment(String trackCode) {
        Shipments shipment = shipmentsRepository.findByTrackCode(trackCode).orElseThrow(() -> new IllegalArgumentException("Invalid track code"));
        shipmentsRepository.delete(shipment);

        ShipmentEvents event = ShipmentEvents.builder()
                .shipmentId(shipment.getId())
                .stage(Stage.DELETED)
                .comment("Successfully deleted shipment")
                .build();
        shipmentEventsRepository.save(event);
    }
}
