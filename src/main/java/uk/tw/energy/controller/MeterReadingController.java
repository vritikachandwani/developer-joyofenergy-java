package uk.tw.energy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.exception.InvalidMeterReadingException;
import uk.tw.energy.exception.MeterReadingNotPresentException;
import uk.tw.energy.service.MeterReadingService;


@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
   //private final MeterReadingValidator meterReadingValidator;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/store")

//    public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
//        if (!isMeterReadingsValid(meterReadings)) {
//            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid meter readings");
//        }
//        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
//        //return ResponseEntity.ok().build();
//        return ResponseEntity.ok("Readings stored successfully");
//    }

    private boolean isMeterReadingsValid(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    public ResponseEntity<String> storeReadings(@RequestBody MeterReadings meterReadings) {
        try {
            if (!isMeterReadingsValid(meterReadings)) {
                throw new InvalidMeterReadingException("Invalid meter readings");
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid meter readings");
            }
            meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
            return ResponseEntity.ok("Readings stored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error storing meter readings");
        }
    }

    @GetMapping("/read/{smartMeterId}")
//    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
//        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
//        return readings.isPresent()
//                ? ResponseEntity.ok(readings.get())
//                : ResponseEntity.notFound().build();
//    }


    public ResponseEntity<?> readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
//        return readings.isPresent()
//                ? ResponseEntity.ok(readings.get())
//                : ResponseEntity.notFound().build();
        if(readings.isPresent() && !readings.get().isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(readings.get());
        }else {
            throw new MeterReadingNotPresentException("No meter reading present for Meter Id: "+smartMeterId);
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No meter reading present for Meter Id: "+smartMeterId);
        }
}}
