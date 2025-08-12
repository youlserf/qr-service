package com.example.qr.service;


import com.example.qr.exception.QrGenerationException;

public interface QrService {
    byte[] generateQrCode(String text) throws QrGenerationException;
    byte[] generateAndMeasure(String text) throws QrGenerationException;
}

