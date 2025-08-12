package com.example.qr.controller;

import com.example.qr.exception.QrGenerationException;
import com.example.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrController {

    private static final String QR_FILENAME = "qr.png";
    private final QrService qrService;

    @GetMapping
    public ResponseEntity<byte[]> generate(@RequestParam String text) throws QrGenerationException {
        byte[] qrImage = qrService.generateQrCode(text);
        return createQrImageResponse(qrImage);
    }

    @GetMapping("/measure")
    public ResponseEntity<byte[]> generateMeasured(@RequestParam String text) throws QrGenerationException {
        byte[] qrImage = qrService.generateAndMeasure(text);
        return createQrImageResponse(qrImage);
    }

    private ResponseEntity<byte[]> createQrImageResponse(byte[] qrImage) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + QR_FILENAME)
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
}
