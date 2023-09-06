# SimpleClient
file sender via http

ориентирован на передачу файлов через http для simple server .
Запускается с параметрами "имя_сервера" "каталог" , например :java -jar "SimpleClient-1.0.0-SNAPSHOT-jar-with-dependencies.jar" 10.10.10.10 D:\files

По умолчанию использует для подключения localhost и каталог D:\\Java\\jars\\files .
При запуске сканирует каталог на предмет файлов для отправки, и отправляет на указанный сервер , по очереди, на 4 порта 8001,8002,8003,8004 (на каждый порт по файлу, если файлов больше 4,
то нумерация снова переключается на 8001 порт и далее).
при получении подтверждении о передаче , сохраняет переданне названия файлов в корневой каталог приложения (файл "fileTable") .
в случае ошибки при передаче (любом ответе клиенту отличающимся от 200),- сохраняет названия не переданного файла в файл "failedFile" .

работа клиента настроена на 2-х минутый таймер с момента отправки , который принудительно отключает передачу данных и выходит из приложения в случае превышения времени таймера.
