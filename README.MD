## Репозиторий содержит патчи для EDT

### на 2023.05 только возможность использования 8.3.23 версии в EDT 2022.2.5

1. Установка скопировать релизный jar в папку dropins EDT
2. Добавить в 1cedt.ini в конец "-Dosgi.framework.extensions=org.eclipse.equinox.weaving.hook"
3. Запустить edt.exe -console
4. выполнить в консоли lb, убедиться что появилась строка dev.etsys.patch
5. выполнить в консоли sta dev.etsys.patch