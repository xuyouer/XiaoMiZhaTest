package ltd.xiaomizha.xuyou.common.utils.convert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * 文件转Base64工具类
 */
@Slf4j
@Component
public class FileToBase64Util {

    /**
     * 将 MultipartFile 转换为 Base64 字符串
     *
     * @param file 上传的文件
     * @return Base64 编码字符串, 不含前缀
     * @throws Exception 文件读取异常
     */
    public String convertToBase64(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 MultipartFile 转换为带前缀的 Base64 Data URI
     * <p>
     * 格式: data:{mimeType};base64,{base64String}
     *
     * @param file 上传的文件
     * @return Base64 Data URI 字符串
     * @throws Exception 文件读取异常
     */
    public String convertToDataUri(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String mimeType = file.getContentType();
        String base64 = convertToBase64(file);

        return String.format("data:%s;base64,%s", mimeType, base64);
    }

    /**
     * 将本地文件路径转换为 Base64 字符串
     *
     * @param filePath 文件绝对路径
     * @return Base64 编码字符串, 不含前缀
     * @throws Exception 文件读取异常
     */
    public String convertFileToBase64(String filePath) throws Exception {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (InputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] bytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }

    /**
     * 将本地文件转换为带前缀的 Base64 Data URI
     *
     * @param filePath 文件绝对路径
     * @param mimeType 文件的 MIME 类型, 如: image/png, application/pdf
     *                 如果为 null, 则根据文件扩展名自动推断
     * @return Base64 Data URI 字符串
     * @throws Exception 文件读取异常
     */
    public String convertFileToDataUri(String filePath, String mimeType) throws Exception {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        String base64 = convertFileToBase64(filePath);

        // 如果未指定 mimeType, 则根据文件扩展名推断
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = inferMimeTypeFromFile(filePath);
        }

        return String.format("data:%s;base64,%s", mimeType, base64);
    }

    /**
     * 将字节数组转换为 Base64 字符串
     *
     * @param bytes 文件字节数组
     * @return Base64 编码字符串
     */
    public String convertBytesToBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("字节数组不能为空");
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将字节数组转换为带前缀的 Base64 Data URI
     *
     * @param bytes    文件字节数组
     * @param mimeType 文件的 MIME 类型
     * @return Base64 Data URI 字符串
     */
    public String convertBytesToDataUri(byte[] bytes, String mimeType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("字节数组不能为空");
        }
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);
        return String.format("data:%s;base64,%s", mimeType, base64);
    }

    /**
     * 从 Base64 字符串解码为字节数组
     *
     * @param base64Str Base64 编码字符串
     * @return 解码后的字节数组
     */
    public byte[] decodeFromBase64(String base64Str) {
        if (base64Str == null || base64Str.isBlank()) {
            throw new IllegalArgumentException("Base64字符串不能为空");
        }
        return Base64.getDecoder().decode(base64Str);
    }

    /**
     * 将 Base64 字符串保存为文件
     *
     * @param base64Str  Base64 编码字符串
     * @param outputPath 保存的文件路径
     * @throws Exception 文件写入异常
     */
    public void saveBase64ToFile(String base64Str, String outputPath) throws Exception {
        if (base64Str == null || base64Str.isBlank()) {
            throw new IllegalArgumentException("Base64字符串不能为空");
        }
        if (outputPath == null || outputPath.isBlank()) {
            throw new IllegalArgumentException("输出路径不能为空");
        }

        byte[] bytes = decodeFromBase64(base64Str);

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(bytes);
            fos.flush();
            log.info("Base64已保存到文件: {}, 大小={} bytes", outputPath, bytes.length);
        }
    }

    /**
     * 根据文件扩展名推断 MIME 类型
     *
     * @param filePath 文件路径
     * @return MIME 类型字符串
     */
    private String inferMimeTypeFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return "application/octet-stream";
        }

        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt", "text" -> "text/plain";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "html", "htm" -> "text/html";
            case "mp3" -> "audio/mpeg";
            case "mp4" -> "video/mp4";
            default -> "application/octet-stream";
        };
    }

}
