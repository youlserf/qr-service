package com.example.qr.service.impl;

import com.example.qr.exception.QrGenerationException;
import com.example.qr.qr.QrGenerator;
import com.example.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrServiceImpl implements QrService {

    private final QrGenerator qrGenerator;

    @Override
    @Cacheable(value = "qrCache", key = "#text", unless = "#result == null")
    public byte[] generateQrCode(String text) throws QrGenerationException {
        log.info("Generando código QR para el texto (longitud={})", text.length());
        return qrGenerator.generate(text);
    }


    @Override
    // @LogExecutionTime // <-- ¡Así se usaría con AOP! (ver explicación extra)
    public byte[] generateAndMeasure(String text) throws QrGenerationException {
        long start = System.nanoTime();
        byte[] qr = this.generateQrCode(text); // Reutilizamos el método cacheable
        long end = System.nanoTime();

        log.info("Tiempo de generación de QR para texto (longitud={}): {:.2f} ms", text.length(), (end - start) / 1_000_000.0);

        return qr;
    }
}