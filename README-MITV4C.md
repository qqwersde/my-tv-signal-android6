# 小米电视 4C（Android 6）适配说明

这是基于 [qqwersde/my-tv-signal-android6](https://github.com/qqwersde/my-tv-signal-android6) 的个人适配，原始项目是 [CleanYourTV](https://github.com/Gathin23/cleanyourtv)。保留原项目及字体许可证。它不是原项目的官方发布版。

## 已测试的设备与范围

- 小米电视 4C：系统报告 `MiTV4C`、设备代号 `matrix`、Android 6.0.1（API 23）、固件 `8.8.20.1275`。其他型号和固件未验证。
- 实测桌面打开、遥控器方向键、Bilibili 投屏及声音，开启主页辅助功能后投屏未中断；一次重启后进入新桌面且投屏正常。
- 小米原桌面和投屏服务保留。主页键可能先出现原桌面，再延迟切回；重定向依赖辅助功能、使用情况访问与系统事件，开机和其他播放场景不保证可靠。
- 默认栏固定云视听小电视，未找到应用时跳过；其他设备可修改 `launcher/app/src/main/res/values/config.xml`。
- 天气从电视保存的位置信息读取经纬度，发送给 Open-Meteo 获取天气。数据可能在应用内缓存 30 分钟。
- 更换桌面不移除开机广告，也不屏蔽视频应用内广告。

## 编译

使用 JDK 17 或 21、Android SDK platform 34 和 build-tools 34.0.0。从仓库的 `launcher` 目录执行：

```sh
./gradlew --no-daemon -PlauncherMinSdk=23 testReleaseUnitTest lintRelease assembleRelease
```

生成的文件是 `launcher/app/build/outputs/apk/release/app-release.apk`。必须指定 `-PlauncherMinSdk=23`；默认构建仍要求 Android 8。安装前确认设备支持侧载，先作为普通应用启动和测试；**不要在此机型上直接运行仓库中的自动清理或默认桌面安装脚本**。可通过 ADB 手动启动：

```sh
adb shell am start -n com.gathin.tvhome/com.gathin.tvhome.MainActivity
```

## 主页键与恢复

Android 6 上辅助服务通过本机使用情况事件识别小米原桌面，并等待一段时间后尝试打开本桌面。这是兼容性方案，不等同于修改系统默认主页。它需要开启 `com.gathin.tvhome/.HomeKeyService` 辅助服务及该应用的使用情况访问权限；**保留其他已开启的辅助服务**。这些权限能观察本机窗口或使用事件，应由设备所有者决定是否启用。

如果播放、投屏或遥控器受到影响，在电视的辅助功能设置中关闭本应用的主页服务，并撤销其使用情况访问；小米原桌面仍可使用。不要禁用、隐藏或卸载小米桌面及投屏服务来强行接管。调试完成后建议关闭 ADB，并避免向互联网暴露调试端口。

本次改动的 Android 构建、Lint 和 230 项单元测试通过；以上实机测试不代表所有旧款小米电视都兼容。完整项目和上游说明保留在仓库中。
