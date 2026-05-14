package ru.shatskikh.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelegramService {

    @Value("${bot.token}")
    private String botToken;

    public boolean isDataValid(Map<String, String> params) {
        try {
            String hash = params.get("hash");
            if (hash == null) return false;

            // Создаем строку для проверки: сортируем ключи и соединяем их через \n
            String dataCheckString = params.entrySet().stream()
                    .filter(e -> !e.getKey().equals("hash")) // Хеш не участвует в проверке
                    .sorted(Map.Entry.comparingByKey())
                    // Формируем строку: key=value
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            // Вычисляем секретный ключ
            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] secretKey = mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

            // Вычисляем финальный хеш
            SecretKeySpec dataCheckKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            Mac mac2 = Mac.getInstance("HmacSHA256");
            mac2.init(dataCheckKeySpec);
            byte[] calculatedHashBytes = mac2.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(calculatedHashBytes).equals(hash);
        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}