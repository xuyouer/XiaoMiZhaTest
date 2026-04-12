package ltd.xiaomizha.xuyou.common.utils.license;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LicenseGeneratorMain {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     XIAOMIZHA License Generator v1.0             ║");
        System.out.println("║     License 文件生成工具                            ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        if (args.length > 0) {
            handleCommandLineArgs(args);
        } else {
            runInteractiveMode();
        }
    }

    private static void handleCommandLineArgs(String[] args) {
        String command = args[0].toLowerCase();
        switch (command) {
            case "genkey":
                if (args.length >= 3) {
                    String pubPath = args[1];
                    String priPath = args[2];
                    generateKeyPair(pubPath, priPath);
                } else {
                    printUsage();
                }
                break;
            case "generate":
                if (args.length >= 2) {
                    String configPath = args[1];
                    generateFromConfig(configPath);
                } else {
                    printUsage();
                }
                break;
            default:
                printUsage();
                break;
        }
    }

    private static void runInteractiveMode() {
        while (true) {
            System.out.println("\n请选择操作:");
            System.out.println("  1. 生成 RSA 密钥对");
            System.out.println("  2. 生成 License 文件");
            System.out.println("  3. 批量生成 License");
            System.out.println("  0. 退出");
            System.out.print("\n请输入选项 [0-3]: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    interactiveGenerateKeyPair();
                    break;
                case "2":
                    interactiveGenerateLicense();
                    break;
                case "3":
                    interactiveBatchGenerate();
                    break;
                case "0":
                    System.out.println("感谢使用 XIAOMIZHA License Generator");
                    return;
                default:
                    System.out.println("无效选项, 请重新输入。");
                    break;
            }
        }
    }

    private static void interactiveGenerateKeyPair() {
        System.out.println("\n--- 生成 RSA 密钥对 ---");
        System.out.print("公钥输出路径 [./keys/license_public.key]: ");
        String pubPath = scanner.nextLine().trim();
        if (StrUtil.isBlank(pubPath)) {
            pubPath = "./keys/license_public.key";
        }

        System.out.print("私钥输出路径 [./keys/license_private.key]: ");
        String priPath = scanner.nextLine().trim();
        if (StrUtil.isBlank(priPath)) {
            priPath = "./keys/license_private.key";
        }

        generateKeyPair(pubPath, priPath);
    }

    private static void generateKeyPair(String publicKeyPath, String privateKeyPath) {
        try {
            System.out.println("\n正在生成 RSA 密钥对...");
            LicenseCryptUtils.generateKeyPairFiles(publicKeyPath, privateKeyPath);

            String pubContent = FileUtil.readUtf8String(publicKeyPath);
            String priContent = FileUtil.readUtf8String(privateKeyPath);

            System.out.println("✓ 密钥对生成成功");
            System.out.println("  公钥文件: " + FileUtil.getAbsolutePath(publicKeyPath));
            System.out.println("  私钥文件: " + FileUtil.getAbsolutePath(privateKeyPath));
            System.out.println("\n公钥内容 (Base64):");
            System.out.println("  " + pubContent.substring(0, Math.min(64, pubContent.length())) + "...");
            System.out.println("\n⚠️  请妥善保管私钥文件, 不要泄露给他人");
        } catch (Exception e) {
            System.err.println("✗ 密钥对生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void interactiveGenerateLicense() {
        System.out.println("\n--- 生成 License 文件 ---");

        System.out.print("私钥文件路径 [./keys/license_private.key]: ");
        String privateKeyPath = scanner.nextLine().trim();
        if (StrUtil.isBlank(privateKeyPath)) {
            privateKeyPath = "./keys/license_private.key";
        }

        if (!FileUtil.exist(privateKeyPath)) {
            System.err.println("✗ 私钥文件不存在: " + privateKeyPath);
            return;
        }

        LicenseGeneratorUtils.LicenseParams params = new LicenseGeneratorUtils.LicenseParams();

        System.out.print("授权给 (用户名/组织名) [XiaoMiZha User]: ");
        String issuedTo = scanner.nextLine().trim();
        params.issuedTo = StrUtil.isBlank(issuedTo) ? "XiaoMiZha User" : issuedTo;

        System.out.print("公司名称 [XiaoMiZha Ltd.]: ");
        String companyName = scanner.nextLine().trim();
        params.companyName = StrUtil.isBlank(companyName) ? "XiaoMiZha Ltd." : companyName;

        System.out.println("可选授权类型: TRIAL, BASIC, PREMIUM, STANDARD, PROFESSIONAL, ENTERPRISE, CUSTOM");
        System.out.print("授权类型 [BASIC]: ");
        String licenseTypeStr = scanner.nextLine().trim().toUpperCase();
        if (StrUtil.isBlank(licenseTypeStr)) {
            params.licenseType = LicenseType.BASIC;
        } else {
            try {
                params.licenseType = LicenseType.valueOf(licenseTypeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("未知类型, 使用默认值 BASIC");
                params.licenseType = LicenseType.BASIC;
            }
        }

        System.out.print("有效天数 [365]: ");
        String expireDaysStr = scanner.nextLine().trim();
        try {
            params.expireDays = StrUtil.isBlank(expireDaysStr) ? 365 : Integer.parseInt(expireDaysStr);
        } catch (NumberFormatException e) {
            params.expireDays = 365;
        }

        System.out.print("硬件绑定ID (可选, 留空表示不绑定): ");
        params.hardwareId = scanner.nextLine().trim();
        if (StrUtil.isBlank(params.hardwareId)) {
            params.hardwareId = null;
        }

        System.out.print("最大并发用户数 (可选, 留空表示不限制): ");
        String maxUsersStr = scanner.nextLine().trim();
        try {
            params.maxConcurrentUsers = StrUtil.isBlank(maxUsersStr) ? null : Integer.parseInt(maxUsersStr);
        } catch (NumberFormatException e) {
            params.maxConcurrentUsers = null;
        }

        System.out.print("功能特性列表 (可选, JSON数组格式或逗号分隔): ");
        params.features = scanner.nextLine().trim();
        if (StrUtil.isBlank(params.features)) {
            params.features = null;
        }

        System.out.print("产品版本 [1.0.0]: ");
        String version = scanner.nextLine().trim();
        params.productVersion = StrUtil.isBlank(version) ? "1.0.0" : version;

        System.out.print("是否将公钥嵌入License文件? (y/n) [y]: ");
        String embedPubKey = scanner.nextLine().trim().toLowerCase();
        String publicKeyBase64 = null;
        if (!"n".equals(embedPubKey)) {
            String defaultPubPath = privateKeyPath.replace("_private", "_public");
            System.out.print("公钥文件路径 [" + defaultPubPath + "]: ");
            String pubKeyPath = scanner.nextLine().trim();
            if (StrUtil.isBlank(pubKeyPath)) {
                pubKeyPath = defaultPubPath;
            }
            if (FileUtil.exist(pubKeyPath)) {
                publicKeyBase64 = FileUtil.readUtf8String(pubKeyPath).trim();
            } else {
                System.out.println("⚠ 公钥文件不存在, 跳过嵌入公钥");
            }
        }

        System.out.print("输出文件路径 [./licenses/license.lic]: ");
        String outputPath = scanner.nextLine().trim();
        if (StrUtil.isBlank(outputPath)) {
            outputPath = "./licenses/license.lic";
        }

        doGenerate(params, privateKeyPath, outputPath, publicKeyBase64);
    }

    private static void doGenerate(LicenseGeneratorUtils.LicenseParams params, String privateKeyPath,
                                   String outputPath, String publicKeyBase64) {
        try {
            System.out.println("\n正在生成 License 文件...");

            LicenseGeneratorUtils.generate(params, privateKeyPath, outputPath, publicKeyBase64);

            System.out.println("✓ License 文件生成成功");
            System.out.println("  输出文件: " + FileUtil.getAbsolutePath(outputPath));
            System.out.println("  授权对象: " + params.issuedTo);
            System.out.println("  公司: " + params.companyName);
            System.out.println("  类型: " + params.licenseType.name());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expireAt = now.plusDays(params.expireDays);
            System.out.println("  有效期: " + now.format(DT_FMT) + " ~ " + expireAt.format(DT_FMT));
            System.out.println("  硬件绑定: " + (params.hardwareId != null ? params.hardwareId : "无"));
            System.out.println("  最大并发: " + (params.maxConcurrentUsers != null ? params.maxConcurrentUsers : "无限制"));
        } catch (Exception e) {
            System.err.println("✗ License 生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void interactiveBatchGenerate() {
        System.out.println("\n--- 批量生成 License ---");
        System.out.print("私钥文件路径 [./keys/license_private.key]: ");
        String privateKeyPath = scanner.nextLine().trim();
        if (StrUtil.isBlank(privateKeyPath)) {
            privateKeyPath = "./keys/license_private.key";
        }

        if (!FileUtil.exist(privateKeyPath)) {
            System.err.println("✗ 私钥文件不存在: " + privateKeyPath);
            return;
        }

        System.out.print("要生成的数量: ");
        int count;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("✗ 无效的数字");
            return;
        }

        System.out.print("授权类型 [BASIC]: ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        LicenseType type;
        try {
            type = StrUtil.isBlank(typeStr) ? LicenseType.BASIC : LicenseType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            type = LicenseType.BASIC;
        }

        System.out.print("有效天数 [365]: ");
        int days;
        try {
            days = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            days = 365;
        }

        System.out.print("输出目录 [./licenses/]: ");
        String outputDir = scanner.nextLine().trim();
        if (StrUtil.isBlank(outputDir)) {
            outputDir = "./licenses/";
        }
        if (!outputDir.endsWith("/") && !outputDir.endsWith("\\")) {
            outputDir += "/";
        }

        String pubKeyPath = privateKeyPath.replace("_private", "_public");
        String publicKeyBase64 = null;
        if (FileUtil.exist(pubKeyPath)) {
            publicKeyBase64 = FileUtil.readUtf8String(pubKeyPath).trim();
        }

        for (int i = 1; i <= count; i++) {
            LicenseGeneratorUtils.LicenseParams params = new LicenseGeneratorUtils.LicenseParams();
            params.issuedTo = "User-" + String.format("%04d", i);
            params.companyName = "XiaoMiZha Ltd.";
            params.licenseType = type;
            params.expireDays = days;
            params.productVersion = "1.0.0";

            String outputPath = outputDir + "license_" + String.format("%04d", i) + ".lic";

            try {
                LicenseGeneratorUtils.generate(params, privateKeyPath, outputPath, publicKeyBase64);
                System.out.println("✓ [" + i + "/" + count + "] " + outputPath);
            } catch (Exception e) {
                System.err.println("✗ [" + i + "/" + count + "] 生成失败: " + e.getMessage());
            }
        }

        System.out.println("\n批量生成完成! 共生成 " + count + " 个 License 文件");
    }

    private static void generateFromConfig(String configPath) {
        System.out.println("从配置文件生成 License: " + configPath);
        System.out.println("(配置文件功能开发中...)");
    }

    private static void printUsage() {
        System.out.println("\n用法:");
        System.out.println("  java LicenseGeneratorMain              - 交互模式");
        System.out.println("  java LicenseGeneratorMain genkey <公钥路径> <私钥路径>  - 生成密钥对");
        System.out.println("  java LicenseGeneratorMain generate <配置文件路径>      - 从配置生成");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java LicenseGeneratorMain genkey ./pub.key ./pri.key");
        System.out.println("  java LicenseGeneratorMain generate license_config.json");
    }
}
