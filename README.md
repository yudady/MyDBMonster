# DBMonster-數據庫數據自動生成工具


RunMainApp


---


DBMonster的schema文件

> schema文件描述了產生數據的規則，在DBMonster中，數據的產生是通過Generator生成的，DBMonster中缺省的數據Generator包括兩個Key Generator（用於產生不重複的數據，分別為MaxKeyGenerator和StringKeyGenerator）和 10 個Data Generator。以下簡單說明一下Data Generator的使用。


> BinaryGenerator用於從外部文件中獲取二進制數據並插入相應字段，該Generator有兩個屬性，分別為file和nulls，file屬性描述數據來源，而nulls屬性則給出該字段生成null的幾率。


> BooleanGenerator用於產生bool型數據，該Generator包括兩個屬性，分別為probability和nulls，probability屬性描述產生true值數據的幾率，nulls屬性給出生成null的幾率。

> ConstantGenerator用於產生固定值的數據，該Generator只有一個屬性constant，給出要插入數據庫的值；


> DateTimeGenerator用於產生DateTime型數據，該Generator包括四個屬性，分別是startDate，endDate，returnedType和nulls，startDate描述開始時間，endDate描述終止時間，格式為「yyyy-mm-dd hh24:MM:ss」；returnedType描述生成數據的類型，可以為date、time或是timestamp；


> DirectoryGenerator用於根據本地文件（字典）的條目向數據庫插入數據，該Generator包括兩個屬性，分別為dictFile和unique，dictFile指明字典文件所在的位置，unique指明產生的數據是unique的還是random的；


> ForeignKeyGenerator用於為設置了外鍵的字段生成數據，該Generator包含兩個屬性，分別是tableName和columnName，tableName指明外鍵引用的表名，columnName指明外鍵引用的字段名；


> NullGenerator用於產生null類型的數據，該Generator不帶任何參數（只產生null）；


> NumberGenerator用戶產生數值類型的數據，該Generator包括5個屬性，分別是minValue、maxValue、returnedType、scale和nulls。其中，minValue和maxValue分別給出產生值的下邊界和上邊界；returnedType給出生成數據的類型，可以是short、integer、long、float、double和numeric類型；scale指明小數位數；nulls表示產生null的幾率；


> StringGenerator用於產生字符串類型的數據，該Generator包括5個屬性，分別是minLength、maxLength、allowSpaces、excludeChars和nulls。其中，minLength和maxLength限定了字符串長度；allowSpaces控制字符串中是否包含空格；excludeChars排除產生字符串時不使用的字符；nulls表示產生null的幾率；


> StringChoiceGenerator用於從給定的字符串中隨機挑選一個作為字段內容，該Generator包含兩個屬性，分別是choice和nulls。其中choice是以逗號分隔的字符串，逗號分隔開的每個內容是一個字段可用的內容。




















