package com.example.qr.service.impl;

import com.example.qr.exception.QrGenerationException;
import com.example.qr.qr.QrGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QrServiceImplTest {

    @Mock
    private QrGenerator qrGenerator;

    @InjectMocks
    private QrServiceImpl qrService;

    @Test
    void generateQrCode_Success() throws Exception {
        String text = "hello";
        byte[] qrData = new byte[] {1, 2, 3};

        Mockito.when(qrGenerator.generate(text)).thenReturn(qrData);

        byte[] result = qrService.generateQrCode(text);

        Assertions.assertArrayEquals(qrData, result);
    }

    @Test
    void generateQrCode_ExceptionPropagated() throws Exception {
        String text = "fail";

        Mockito.when(qrGenerator.generate(text)).thenThrow(new QrGenerationException("fail"));

        Assertions.assertThrows(QrGenerationException.class, () -> {
            qrService.generateQrCode(text);
        });
    }

    @Test
    void generateAndMeasure_ReturnsQrData() throws Exception {
        String text = "measure";
        byte[] qrData = new byte[] {4, 5, 6};

        Mockito.when(qrGenerator.generate(text)).thenReturn(qrData);

        byte[] result = qrService.generateAndMeasure(text);

        Assertions.assertArrayEquals(qrData, result);
    }
}

