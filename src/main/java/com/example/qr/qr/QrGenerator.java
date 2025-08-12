package com.example.qr.qr;

import com.example.qr.exception.QrGenerationException;

public interface QrGenerator {
    byte[] generate(String text) throws QrGenerationException;
}
