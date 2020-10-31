# bank-service

### Задача:
Надо написать веб-приложение, цель которого - операции со счетами пользователей.
Должно быть 3 API (RESTful) - перевод денег с одного счёта на другой, положить деньги на счёт, снять деньги со счёта. Счет не может уйти в овердрафт(минусовой остаток счета).
В качестве хранилища можно использовать любую реляционную БД.
Исходный код должен собираться с помощью maven или gradle в исполняемый jar. Решение должно быть на Java или Kotlin и должно быть покрыто тестами.

### Что сделано:
```
POST /auth - получение JWT, ожидает username и password (admin/password)
*POST /account - создание счета с нулевым балансом и uuid
*POST /transaction/withdraw - зачисление денег на счет
*POST /transaction/deposit - снятие денег со счета
*POST /transaction/transfer - перевод денег со счета на счет
```
Помеченные звездочкой запросы недоступны неавторизованным пользователям.

## Бизнес-логика:
- Для работы с данными о балансе\объеме транзакции выбран тип BigDecimal, так как в отличие от double, он обеспечивает нужную точность и корректное округление (Banker's rounding). Также невозможность переполнения типа избавляет от лишних проверок впоследствии. В бд поставил тип decimal.
- В моей интерпретации тз, изменить состояние счета можно только с помощью предоставленных API операций. Новый аккаунт будет создан с нулем на счету, выполняю проверку результатов операции до изменения полей бд. В результате пришел к тому, что сервис должен работать не с пользователями, а со счетами (может быть много у одного пользователя), которые имеют uuid и привязаны к пользователю.
- Необходимо сохранять данные о выполненных транзакциях.
- По дополнительным полям - типу и дате транзакции - можно выстроить историю работы со счетом, которая будет необходима, например, при обращении клиента.

## Безопасность:
- Для передачи параметров запросов было принято решение использовать RequestBody как более безопасное по сравнению с PathVariable. Так мы исключим самый очевидный способ доступа к объектам подбором значений для /{id}, но все еще возможен нежелательный доступ к функционалу API, если известна формулировка тела запроса. Чтобы это исключить, добавлю авторизацию с помощью JWT.
- Учитывая вышесказанное, для работы с запросами, инициирующими транзакции был сделан выбор в пользу POST запросов. Дополнительным преимуществом является отсутствие кеширования и фиксации их в логах.
- Возвращаемый ответ с описанием объекта также кажется мне слабым местом системы, т.к. работа с деньгами и операциями над ними предполагает максимально возможную приватность. Ответы сообщают только о статусе.
- В стремлении сделать API более закрытым, упростил запросы для работы с таблицей аккаунтов и модель аккаунта. Впоследствии вообще оставил только метод для создания аккаунта с нулевым балансом, т.к. удаление непустого аккаунта должно быть опосредовано дополнительной логикой.
- Дефолтный long id для аккаунтов с предсказуемым автоинкрементом не показался мне достаточно хорошей идеей, заменил на uuid.

## Многопоточность:
Для обеспечения корректного параллельного доступа к бд (например, в случае кластера сервисов) при работе с деньгами было принято решение использовать Transactional с уровнем изоляции Repeatable Read для блокировки полей, с которыми взаимодейтвует каждая из параллельных транзакций. В случае доступа к заблокированному полю транзакция будет выполнена повторно. Количество возможных попыток ограничено 5 во избежание дедлоков. 

### Что еще хотелось бы сделать:
- Rate limit запросов для предотвращения атак.
- Отделить сервис авторизации и выдачи токенов от основного сервиса. Добавить группы пользователей с разными правами.
- Тип и timestamp для транзакций.
- Модель и сервис для клиента с информацией о нем и его счетах.
- Кодирование тела запросов.
