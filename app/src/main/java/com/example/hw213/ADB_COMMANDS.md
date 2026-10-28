
перевірка підключення

```bash
adb devices
```

Відключити зарядку

```bash
adb shell dumpsys battery unplug
```

Підключити зарядку

```bash
adb shell dumpsys battery set ac 1
```


У літаку увімкнути

```bash
adb shell cmd connectivity airplane-mode enable
```

У літаку вимкнути

```bash
adb shell cmd connectivity airplane-mode disable
```

Низький заряд (спершу відключити зарядку)

```bash
adb shell dumpsys battery set level 5
```

Заряд відновлено

```bash
adb shell dumpsys battery set level 80
```

Вимкнути екран

```bash
adb shell input keyevent KEYCODE_SLEEP
```

Увімкнути екран

```bash
adb shell input keyevent KEYCODE_WAKEUP
```

Вимкнути автоповорот

```bash
adb shell settings put system accelerometer_rotation 0
```

Альбомна орієнтація

```bash
adb shell settings put system user_rotation 1
```

Портретна орієнтація

```bash
adb shell settings put system user_rotation 0
```

Темна тема

```bash
adb shell cmd uimode night yes
```

Світла тема

```bash
adb shell cmd uimode night no
```

Великий шрифт

```bash
adb shell settings put system font_scale 1.3
```

Звичайний шрифт

```bash
adb shell settings put system font_scale 1.0
```

Скинути батарею

```bash
adb shell dumpsys battery reset
```

Увімкнути автоповорот

```bash
adb shell settings put system accelerometer_rotation 1
```
