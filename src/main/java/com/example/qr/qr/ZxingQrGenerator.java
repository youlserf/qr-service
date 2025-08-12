package com.example.qr.qr;

import com.example.qr.exception.QrGenerationException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Component
public class ZxingQrGenerator implements QrGenerator {

    // Constantes privadas y finales
    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    private static final int MAX_LENGTH = 200;
    private static final String IMAGE_FORMAT = "PNG";
    private static final String CHARACTER_SET = "UTF-8";

    @Override
    public byte[] generate(String text) throws QrGenerationException {
        validateInput(text);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, CHARACTER_SET);

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            // Envolver la excepción original para no perder el stack trace.
            throw new QrGenerationException("Error generando el código QR", e);
        }
    }

    private void validateInput(String text) throws QrGenerationException {
        if (text == null || text.trim().isEmpty()) {
            throw new QrGenerationException("El texto no puede ser nulo o vacío.");
        }
        if (text.length() > MAX_LENGTH) {
            throw new QrGenerationException("El texto excede la longitud máxima permitida de " + MAX_LENGTH + " caracteres.");
        }
    }
}