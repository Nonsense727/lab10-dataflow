# Лаборатори №10 — Өгөгдлийн урсгалын тест: Definition-Use хосууд

**Оюутан:** Т. Амармэнд
**Оюутны код:** B232270036
**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)
**Репозитор:** https://github.com/Nonsense727/lab10-dataflow

---

## 1. Орчны хувилбар

```
$ export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
$ export PATH="$JAVA_HOME/bin:$PATH"

$ java -version
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment Homebrew (build 21.0.12.1)
OpenJDK 64-Bit Server VM Homebrew (build 21.0.12.1, mixed mode, sharing)

$ mvn -version
Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Maven home: /opt/homebrew/Cellar/maven/3.10.0/libexec
Java version: 21.0.12.1, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@21/21.0.12.1/libexec/openjdk.jdk/Contents/Home
Default locale: en_MN, platform encoding: UTF-8, time zone: Asia/Ulaanbaatar
OS name: "mac os x", version: "27.0.1", arch: "aarch64", family: "mac"
```

`pom.xml` нь `maven.compiler.release=17` тул JDK 21 дээр release 17-оор
compile хийгдэж байна. Бүх `mvn` команд дээр дээрх `JAVA_HOME`-ыг
тохируулсан.

## 2. Төслийн бүтэц

```
lab10-dataflow/
├── pom.xml
├── .gitignore
├── README.md
├── cfg-bonus.png
├── results/
│   ├── mvn-verify-starter.txt
│   ├── mvn-verify-A.txt
│   ├── jacoco-A.csv
│   ├── mvn-verify-alldefs.txt
│   ├── jacoco-alldefs.csv
│   ├── mvn-verify-alldupairs.txt
│   ├── jacoco-alldupairs.csv
│   ├── mvn-test-M1.txt
│   ├── mvn-test-M2.txt
│   ├── mvn-test-M2-kill.txt
│   ├── mvn-test-M3.txt
│   ├── pmd-M3.txt
│   ├── mvn-verify-final.txt
│   └── jacoco-final.csv
└── src/
    ├── main/java/mn/edu/must/sqat/DataFlowTargets.java
    └── test/java/mn/edu/must/sqat/
        ├── GcdTest.java
        └── BonusTest.java
```

`DataFlowTargets.java`-ийг өөрчлөөгүй. Starter commit `1b326ff`-тэй харьцуулбал:

```
$ git diff 1b326ff -- src/main
(гаралт хоосон — 0 мөр)
```

Starter дээр `mvn verify` ажиллуулахад тест байхгүй тул
`No tests to run.`, JaCoCo нь `Skipping JaCoCo execution due to missing
execution data file.` гэж гарсан (`results/mvn-verify-starter.txt`).

## 3. Тестийн ID-ууд

| ID | Тестийн метод |
|---|---|
| G1 | `gcd_12_8_returns4` |
| G2 | `gcd_7_0_returns7` |
| G3 | `gcd_0_5_returns5` |
| G4 | `gcd_0_0_returns0` |
| B1 | `allDefs_belowThreshold_usesScoreDefAt49` |
| B2 | `allDefs_atThreshold_usesScoreDefAt51` |
| B3 | `du_emptyArray_threshold0_returns5` |
| B4 | `du_singlePresent_belowThreshold_returns0` |
| B5 | `du_streakResetByGap_falseSideOfLine42` |
| B6 | `du_null_throws` |
| B7 | `du_negativeThreshold_throws` |
| B8 | `du_totalPresent6_streak4_noStreakBonus` (Хэсэг C-д нэмсэн) |

Бүх тестийн нэр, `@DisplayName` латин үсгээр бичигдсэн.

---

## 4. Хэсэг A — gcd: бүрэн DU хүснэгт

### 4.1 Def/Use жагсаалт (файлын мөрийн дугаараар)

```
13: public static int gcd(int x, int y) {     def: x, y
14:     int tmp;                              (def байхгүй — зөвхөн зарлалт)
15:     while (y != 0) {                      p-use: y
16:         tmp = x % y;                      c-use: x, y   def: tmp
17:         x = y;                            c-use: y      def: x
18:         y = tmp;                          c-use: tmp    def: y
19:     }
20:     return x;                             c-use: x
21: }
```

### 4.2 DU хосууд (бүх 11 хос)

| # | Хувьсагч | Def | Use | c-use/p-use | Def-clear зам | Тест | Хамрагдсан |
|---|---|---|---|---|---|---|---|
| 1 | x | 13 | 16 | c-use | 13 → 15T → 16 | G1 | ✅ |
| 2 | x | 13 | 20 | c-use | 13 → 15F → 20 | G2, G4 | ✅ |
| 3 | x | 17 | 16 | c-use | 17 → 18 → 15T → 16 | G1 | ✅ |
| 4 | x | 17 | 20 | c-use | 17 → 18 → 15F → 20 | G1, G3 | ✅ |
| 5 | y | 13 | 15 | p-use | 13 → 15 | G1 (T), G2 (F) | ✅ |
| 6 | y | 13 | 16 | c-use | 13 → 15T → 16 | G1 | ✅ |
| 7 | y | 13 | 17 | c-use | 13 → 15T → 16 → 17 | G1 | ✅ |
| 8 | y | 18 | 15 | p-use | 18 → 15 | G1 (T), G3 (F) | ✅ |
| 9 | y | 18 | 16 | c-use | 18 → 15T → 16 | G1 | ✅ |
| 10 | y | 18 | 17 | c-use | 18 → 15T → 16 → 17 | G1 | ✅ |
| 11 | tmp | 16 | 18 | c-use | 16 → 17 → 18 | G1 | ✅ |

`tmp`-ийн цорын ганц use нь 18-р мөр (`y = tmp`); `tmp`-ийг 16-р мөрөөс
хэзээ ч дахин тодорхойлохгүй тул (16, 18) хос нь def-clear.

### 4.3 Тестүүд ба тайлбар

| ID | Тест | Оролт | Хүлээлт | Онцлог |
|---|---|---|---|---|
| G1 | `gcd_12_8_returns4` | (12, 8) | 4 | Давталт **2 удаа** |
| G2 | `gcd_7_0_returns7` | (7, 0) | 7 | Давталт **0 удаа** |
| G3 | `gcd_0_5_returns5` | (0, 5) | 5 | Давталт **1 удаа** |
| G4 | `gcd_0_0_returns0` | (0, 0) | 0 | Давталт 0 удаа |

**gcd(7,0) ямар хосыг ганцаараа хамардаг вэ?** Зөвхөн `x`-ийн (13, 20)
хосыг — `x`-ийн 13-р мөрийн def нь 17-р мөрөөр дахин тодорхойлогдохгүйгээр
20-р мөр хүртэл амьд үлдэх цорын ганц тохиолдол бол давталт огт ажиллахгүй
байх нь юм. Мөн `y`-ийн (13, 15) p-use-ийн **F** салаа энд л гарна
(`y = 0`). G4 мөн ижил зан төлөвтэй.

**gcd(12,8) хэдэн давталт хийдэг вэ?** 2 удаа: (1) `tmp = 12 % 8 = 4`,
`x = 8`, `y = 4`; (2) `tmp = 8 % 4 = 0`, `x = 4`, `y = 0`; дараа нь `y != 0`
худал болж `return x = 4`. Хоёр дахь давталт шаардагддаг хосууд нь
`x`-ийн (17, 16), `y`-ийн (18, 15) T салаа, (18, 16), (18, 17) — эдгээр нь
17/18-р мөрөнд def хийгдсэний дараа дахин нэг удаа 15/16/17-р мөр хүрэх
замыг шаарддаг. Зөвхөн G1 (2 давталт) эдгээрийг хамарна; G2/G4 0, G3 1
давталттай тул хамарч чадахгүй.

G1 ба G2 хамтдаа бүх 11 хосыг хамарна; G3, G4 нь давталтын 1 ба 0
тохиолдлын зан төлөвийг нэмж баталгаажуулна. `mvn verify`
(`results/mvn-verify-A.txt`): **Tests run: 4, Failures: 0**.

---

## 5. Хэсэг B — bonus: бүрэн анализ

### 5.1 B1 — CFG (`cfg-bonus.png`)

CFG-г Graphviz-ээр зурсан. Зангилаа бүр файлын мөрийн дугаартай
(31, 32, 33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 46, 49, 50, 51, 53, 54, 56),
ирмэг бүрийн хажууд түүнийг дайрдаг тестийн **бодит метод нэр** бичигдсэн.
`for (int a : attendance)` (38-р мөр) давталтын нөхцөлийг "дараагийн элемент
байна уу" гэж, алхмыг "дараагийн элементийг `a`-д оноох" гэж зурсан —
38-р мөр нь `attendance`-ийн p-use ба `a`-ийн def хоёулаа.

Гол ирмэгүүд:

| Ирмэг | Нөхцөл | Дайрч буй тест |
|---|---|---|
| 31 → 32 | entry | бүх bonus тест |
| 32 → 33 | `null` эсвэл `threshold < 0` = T | B6, B7 |
| 32 → 35 | F | B1–B5 |
| 38 → 39 | дараагийн элемент байна (T) | B1, B2, B4, B5, B8 |
| 38 → 49 | элемент дууссан (F) | B3 |
| 39 → 40 | `a > 0` T | B1, B2, B4, B5, B8 |
| 39 → 46 | `a > 0` F | B1, B2, B5, B8 |
| 42 → 43 | `streak > best` T | B1, B2, B4, B8 |
| 42 → 38 | `streak > best` F | B5, B8 |
| 50 → 51 | `present >= threshold` T | B2, B3 |
| 50 → 53 | F | B1, B4, B5, B8 |
| 53 → 54 | `best >= 5` T | B1, B2 |
| 53 → 56 | F | B3, B4, B5 |

### 5.2 B2 — Def/Use жагсаалт (мөр бүрээр)

```
31: public static int bonus(int[] attendance, int threshold)   def: attendance, threshold
32:     if (attendance == null || threshold < 0) {             p-use: attendance, threshold
33:         throw new IllegalArgumentException("Буруу оролт");  (def/use байхгүй, урсгал тасарна)
34:     }
35:     int present = 0;                                        def: present
36:     int streak = 0;                                         def: streak
37:     int best = 0;                                           def: best
38:     for (int a : attendance) {                              p-use: attendance (has next)
                                                                def: a (дараагийн элемент)
39:         if (a > 0) {                                        p-use: a
40:             present = present + 1;                          c-use: present   def: present
41:             streak = streak + 1;                            c-use: streak    def: streak
42:             if (streak > best) {                            p-use: streak, best
43:                 best = streak;                              c-use: streak    def: best
44:             }
45:         } else {
46:             streak = 0;                                     def: streak (kill)
47:         }
48:     }
49:     int score = 0;                                          def: score
50:     if (present >= threshold) {                             p-use: present, threshold
51:         score = 5;                                          def: score
52:     }
53:     if (best >= 5) {                                        p-use: best
54:         score = score + 3;                                  c-use: score     def: score
55:     }
56:     return score;                                           c-use: score
57: }
```

### 5.3 B3 — DU хүснэгт (23 хос, kill тэмдэглэгээтэй)

"Kill" багана нь тухайн def-ийг зам дээр хөнөөдөг дараагийн def-ийг заана.

| # | Хувьсагч | Def | Use | Төрөл | Def-clear зам | Kill | Тест | Хамрагдсан |
|---|---|---|---|---|---|---|---|---|
| 1 | attendance | 31 | 32 | p-use | 31 → 32 | — | B6 (T), B1 (F) | ✅ |
| 2 | attendance | 31 | 38 | p-use | 31 → 32F → 35 → 36 → 37 → 38 | — | B1 | ✅ |
| 3 | threshold | 31 | 32 | p-use | 31 → 32 | — | B7 (T), B1 (F) | ✅ |
| 4 | threshold | 31 | 50 | p-use | 31 → 32F → 35…49 → 50 | — | B1 | ✅ |
| 5 | present | 35 | 40 | c-use | 35 → 36 → 37 → 38T → 39T → 40 | 40 | B1 | ✅ |
| 6 | present | 35 | 50 | p-use | 35 → 36 → 37 → 38F → 49 → 50 | — (present өдөр байхгүй) | B3 | ✅ |
| 7 | present | 40 | 40 | c-use | 40 → 41 → 42 → 38T → 39T → 40 | 40 | B1 | ✅ |
| 8 | present | 40 | 50 | p-use | 40 → … → 38F → 49 → 50 | — | B1 | ✅ |
| 9 | streak | 36 | 41 | c-use | 36 → 37 → 38T → 39T → 40 → 41 | 41 | B1 | ✅ |
| 10 | streak | 41 | 41 | c-use | 41 → 42 → 38T → 39T → 40 → 41 | 41 | B1 | ✅ |
| 11 | streak | 41 | 42 | p-use | 41 → 42 | — | B1 (T), B5 (F) | ✅ |
| 12 | streak | 41 | 43 | c-use | 41 → 42T → 43 | — | B1 | ✅ |
| 13 | streak | 46 | 41 | c-use | 46 → 38T → 39T → 40 → 41 | — | B1, B5 | ✅ |
| 14 | best | 37 | 42 | p-use | 37 → 38T → 39T → 40 → 41 → 42 | 43 | B1 (T), B5 (F) | ✅ |
| 15 | best | 37 | 53 | p-use | 37 → 38F → 49 → 50 → 53 | — (best def байхгүй) | B3 (F) | ✅ |
| 16 | best | 43 | 42 | p-use | 43 → 38T → 39T → 40 → 41 → 42 | — | B1 (T), B5 (F) | ✅ |
| 17 | best | 43 | 53 | p-use | 43 → 38F → 49 → 50 → 53 | — | B1 (T), B4 (F) | ✅ |
| 18 | a | 38 | 39 | p-use | 38 → 39 | — (элемент бүрт) | B1 (T/F) | ✅ |
| 19 | score | 49 | 54 | c-use | 49 → 50F → 53T → 54 | 51 | B1 | ✅ |
| 20 | score | 49 | 56 | c-use | 49 → 50F → 53F → 56 | — | B4 | ✅ |
| 21 | score | 51 | 54 | c-use | 51 → 53T → 54 | 54 | B2 | ✅ |
| 22 | score | 51 | 56 | c-use | 51 → 53F → 56 | — | B3 | ✅ |
| 23 | score | 54 | 56 | c-use | 54 → 56 | — | B1 | ✅ |

**Kill хийгддэг def-үүд:**

- 40-р мөр `present = present + 1` нь 35-р мөрийн `present` def-ийг хөнөөнө.
- 41-р мөр `streak = streak + 1` нь 36 ба 46-р мөрийн `streak` def-ийг хөнөөнө.
- 43-р мөр `best = streak` нь 37-р мөрийн `best` def-ийг хөнөөнө.
- 46-р мөр `streak = 0` нь 36 ба 41-р мөрийн `streak` def-ийг хөнөөнө.
- 51-р мөр `score = 5` нь 49-р мөрийн `score` def-ийг хөнөөнө.
- 54-р мөр `score = score + 3` нь 51-р мөрийн `score` def-ийг хөнөөнө.

**DU хос болж тооцогдохгүй (def-clear замгүй) хослолууд:**

- `streak` 36 → 42 ба 36 → 43: 36-аас 42/43 хүрэх зам заавал 41-р мөрөөр
  (`streak = streak + 1`) дамждаг тул 36-ийн def 41 дээр хөнөөгдөнө.
- `streak` 46 → 42 ба 46 → 43: 46-аас хойш дахин ирсэн өдөр 41-р мөр
  заавал ажиллаж, 46-ийн def-ийг хөнөөнө.
- `score` 49 → 54-ийн 50T замаар: 50 T үед 51-р мөр `score`-ийг дахин
  тодорхойлдог тул 49-ийн def 54 хүрэхгүй. (49, 54) хос зөвхөн 50F, 53T
  замаар оршино — хүснэгтийн 19-р мөр.
- `present` 35 → 40-ийн "present өдөр байхгүй" зам: 40-р мөр ажиллахын
  тулд 39T шаардлагатай бөгөөд тэр үед 40 нь 35-ыг хөнөөнө. Иймээс 35-ын
  use нь зөвхөн 40 (эхний ирсэн өдөр) эсвэл 50 (огт ирээгүй) байна.

**Биелэшгүй гэж үзсэн салаа:**

- 42-р мөр `streak > best`-ийн **F** салаа нь эхний ирсэн өдөрт боломжгүй:
  тэр үед `streak = 1`, `best = 0` тул `1 > 0` заавал T. F салаа нь
  өмнө нь `best` тодорхойлогдсон (эсвэл `streak` 0 болж reset хийгдсэн)
  дараа л боломжтой — B5 `[1,0,1]` үүнийг хамарна.
- 32-р мөрийн `threshold < 0` операндын T утга нь `attendance == null` үед
  short-circuit-оор огт үнэлэгдэхгүй; тиймээс B6 (`null`) нь зөвхөн
  `attendance`-ийн T салааг, B7 (`threshold = -1`, массив нь `[1]`) нь
  `threshold`-ийн T салааг тус тусад нь хамарна.

Бусад 23 хос бүгд биелэгдэх боломжтой бөгөөд дээрх тестүүдээр хамрагдсан.

### 5.4 B4 — ALL-DEFS шат (2 тест)

`BonusTest.java`-ийн эхний хоёр тест нь all-defs-ийг хангана:

- **B1** `bonus([0,1,1,1,1,1], 6) = 3` — эхний 0 нь 46-р мөрийн
  `streak = 0`-ыг ажиллуулж, дараагийн 5 ирсэн өдөр нь 40, 41, 43-ыг
  тодорхойлно. `present = 5 < 6` тул 50 F, `best = 5` тул 53 T → 49-ийн
  def 54-т хүрнэ. B1 нь 35, 36, 37, 38, 40, 41, 43, 46, 49-ийн def-үүдийг
  хамарна.
- **B2** `bonus([0,1,1,1,1,1], 5) = 8` — ижил массив, `threshold = 5` тул
  50 T → 51-ийн def, 53 T → 54-ийн def, 54 → 56. Ингэснээр 51 ба 54-ийн
  def-үүд хамрагдана.

**Яагаад 2 тестээс цөөн болохгүй вэ?** `score`-ийн 49-р мөрийн def нь
use-дээ зөвхөн 50 F үед хүрнэ; 50 T болмогц 51-р мөр `score`-ийг дахин
тодорхойлж, 49-ийг хөнөөнө. Харин 51-р мөрийн def оршихын тулд 50 заавал
T байх ёстой. Нэг гүйцэтгэлд 50 нь нэгэн зэрэг T ба F байж чадахгүй тул
49 ба 51-ийн def-үүдийг нэг тестээр хамарч болохгүй — доод хязгаар нь
**2 гүйцэтгэл**. B1 ба B2 яг энэ хоёрыг хамарч, бусад бүх def-үүдийг B1
дангаараа хамардаг. (Бусад def бүр нэг л гүйцэтгэлд нийцэж байгаа тул
2 нь хамгийн бага бөгөөд хангалттай тоо.)

`mvn verify` (`results/mvn-verify-alldefs.txt`):

```
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

6 тест = G1–G4 (gcd) + B1–B2 (bonus all-defs). `results/jacoco-alldefs.csv`:

```
INSTRUCTION_MISSED=8   INSTRUCTION_COVERED=73
BRANCH_MISSED=4        BRANCH_COVERED=12
LINE_MISSED=2          LINE_COVERED=22
```

| Хэмжүүр | Хамрагдсан / Нийт | Хувь |
|---|---|---|
| Instruction | 73 / 81 | 90.1% |
| **Branch** | **12 / 16** | **75.0%** |
| **Line** | **22 / 24** | **91.7%** |

All-defs хангагдсан ч branch 100% биш: 32-р мөрийн хоёр T салаа
(`null`, `threshold < 0`), 42-р мөрийн F салаа, 53-р мөрийн F салаа
хамрагдаагүй (4 branch). Line-ийн 2 алдагдал нь 33-р мөр (`throw`) ба
7-р мөрийн далд constructor.

### 5.5 B4 — ALL-DU-PAIRS шат (нэмж 5 тест)

| Тест | Оролт | Хүлээлт | Нэмж хамарч буй хосууд |
|---|---|---|---|
| B3 | `bonus([], 0)` | 5 | (35,50), (37,53) F, (51,56) |
| B4 | `bonus([1], 2)` | 0 | (49,56), (43,53) F |
| B5 | `bonus([1,0,1], 3)` | 0 | (46,41), (41,42) F, (37,42) F, (43,42) F |
| B6 | `bonus(null, 1)` | `IllegalArgumentException` | (31,32) p-use-ийн `attendance` T |
| B7 | `bonus([1], -1)` | `IllegalArgumentException` | (31,32) p-use-ийн `threshold` T |

B1–B7 нийт **7 bonus тест** нь хүснэгтийн 23 хосыг бүгдийг хамарна.
`mvn verify` (`results/mvn-verify-alldupairs.txt`): **Tests run: 11**
(4 gcd + 7 bonus), Failures: 0.

### 5.6 B5 — JaCoCo хэмжилт

| Үе шат | Тест | Branch | Line | Instruction |
|---|---|---|---|---|
| all-defs | 6 | 12 / 16 (75.0%) | 22 / 24 (91.7%) | 73 / 81 (90.1%) |
| all-DU-pairs | 11 | 16 / 16 (100%) | 23 / 24 (95.8%) | 78 / 81 (96.3%) |
| эцсийн (C-ийн дараа) | 12 | 16 / 16 (100%) | 23 / 24 (95.8%) | 78 / 81 (96.3%) |

`results/jacoco-final.csv`:

```
INSTRUCTION_MISSED=3   INSTRUCTION_COVERED=78
BRANCH_MISSED=0        BRANCH_COVERED=16
LINE_MISSED=1          LINE_COVERED=23
METHOD_MISSED=1        METHOD_COVERED=2
```

Branch-ийн 16 нь: gcd-ийн `while` 2 + bonus-ийн 32-р мөр (`||` хоёр
операнд) 4 + 38-р мөрийн for-each 2 + 39 (2) + 42 (2) + 50 (2) + 53 (2) = 16.
`BRANCH_MISSED = 0` — шаардлага хангагдсан. `LINE_MISSED = 1` нь 7-р
мөрийн Java-гийн далд constructor (JaCoCo тайланд `DataFlowTargets()`
улаан, 3 instruction); үүнийг хамруулахын тулд `new DataFlowTargets()`
тест бичих шаардлагатай боловч энэ нь DU хамралтад нэмэргүй тул би
хамруулаагүй — зөвшөөрөгдсөн 1 мөр.

### 5.7 Branch 100% байхад DU хос хамрагдаагүй үлдэх жишээ

Энэ бол лекцийн гол баталгаа: **branch хамралт 100% нь DU хамралт 100%
гэсэн үг биш.** Жишээ нь gcd-д зөвхөн G2 `gcd(7,0)` (0 давталт) ба
G3 `gcd(0,5)` (1 давталт) гэсэн хоёр тест бичвэл:

- `while (y != 0)`-ийн T ба F салаа хоёулаа ажиллана → gcd-ийн branch 2/2
  (100%), мөр бүр ажиллана;
- гэвч 2 дахь давталт огт болохгүй тул `x` (17, 16), `y` (18, 15) T,
  (18, 16), (18, 17) гэсэн 4 хос хамрагдахгүй үлдэнэ.

Өөрөөр хэлбэл branch coverage нь "аль салаа ажилласан бэ" гэдгийг л
хэмждэг бөгөөд "def нь аль use хүртэл, ямар замтайгаар амьд үлдсэн бэ"
гэдгийг хэмждэггүй. Иймээс gcd-д G1 (2 давталт) зайлшгүй хэрэгтэй.
Мөн эсрэг чиглэл ч үнэн: all-defs (6 тест) хангагдсан үед branch ердөө
12/16 байсан — all-defs нь branch хамралтыг ч баталгаажуулахгүй.

---

## 6. Хэсэг C — Fault seeding (урсгалын алдаа тарих)

Мутаци бүрийг `DataFlowTargets.java` дээр шууд хийж, `mvn test` ажиллуулсны
дараа `git restore`-оор буцаасан. Мутацийг commit хийгээгүй.

### 6.1 Мутацийн үр дүнгийн хүснэгт

| № | Мутаци | Төрөл | Тестийн үр дүн | Унасан тест ба мессеж |
|---|---|---|---|---|
| M1 | `streak = streak + 1;` мөрийг устгах | Missing update (дутуу def) | **2 / 11 унасан** | `allDefs_belowThreshold_usesScoreDefAt49:28 expected: <3> but was: <0>`; `allDefs_atThreshold_usesScoreDefAt51:34 expected: <8> but was: <5>` |
| M2 | `if (streak > best) { best = streak; }` → `best = present;` | Wrong variable (буруу use) | **0 / 11 унасан — амьд үлдсэн** | `BUILD SUCCESS`; дараа нь B8 нэмэхэд `du_totalPresent6_streak4_noStreakBonus:60 expected: <5> but was: <8>` |
| M3 | Давталтын дараа `int total = present + best;` нэмэх | Dead definition (үхсэн def) | **0 / 12 унасан** | `BUILD SUCCESS`; PMD: `UnusedLocalVariable` (49-р мөр) |

Файлууд: `results/mvn-test-M1.txt`, `results/mvn-test-M2.txt`,
`results/mvn-test-M2-kill.txt`, `results/mvn-test-M3.txt`, `results/pmd-M3.txt`.

### 6.2 M1 — дутуу update (missing def)

`streak` хэзээ ч өсөхгүй тул `best` 0 хэвээр үлдэж, 53-р мөрийн нөхцөл
хэзээ ч T болохгүй. 5 оноо авдаг тестүүд 3 ба 8-ын оронд 0 ба 5 буцаана.
`results/mvn-test-M1.txt`:

```
[ERROR] Tests run: 11, Failures: 2, Errors: 0, Skipped: 0
[ERROR]   BonusTest.allDefs_atThreshold_usesScoreDefAt51:34 expected: <8> but was: <5>
[ERROR]   BonusTest.allDefs_belowThreshold_usesScoreDefAt49:28 expected: <3> but was: <0>
```

M1 нь `streak`-ийн def-ийг хамарсан DU тестүүдээр шууд баригдсан.

### 6.3 M2 — буруу use: яагаад амьд үлдсэн бэ, юу унагасан бэ

M2-ийн дараа `best` нь `streak` биш, `present`-ийн утгыг авна.
`results/mvn-test-M2.txt`-д бүх 11 тест ногоон (`BUILD SUCCESS`).

**Шалтгаан:** 42-р мөр T болох мөчид `streak > best` байна. Хэрэв массивд
эхнээс хойш (эсвэл сүүлийн `streak = 0`-ээс хойш) тасарсан өдөр байгаагүй
бол `present == streak` байна. B1, B2-ийн `[0,1,1,1,1,1]` массивт 0 нь
зөвхөн эхэнд байгаа тул дараагийн бүх ирсэн өдөрт `present` ба `streak`
тэнцүү — `best = present` нь `best = streak`-тэй ижил утга өгнө. B3, B4-д
`best` 5-аас бага, B5-д `[1,0,1]` үед mutated `best = 2` болсон ч бодит
`best = 1`; хоёулаа 5-аас бага, `threshold = 3` тул оноо ижил 0 гарна.
Иймээс DU хосууд бүгд хамрагдсан ч утгын зөрүү илрэхгүй байна.

**Унагасан тест:** `best`-ийн утга `present`-ээс ялгаатай байх тохиолдол
хэрэгтэй — нийт 6 ирсэн өдөр, гэхдээ хамгийн урт тасралтгүй цуваа нь 4:

```java
@Test
void du_totalPresent6_streak4_noStreakBonus() {
    assertEquals(5, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 0, 1, 1}, 6));
}
```

Бодит код: `present = 6 >= 6` → 5 оноо, `best = 4 < 5` → нэмэлт 3 оноо
байхгүй, нийт **5**. M2-тай: `best = present = 6 >= 5` → 5 + 3 = **8**.
`results/mvn-test-M2-kill.txt`:

```
[ERROR]   BonusTest.du_totalPresent6_streak4_noStreakBonus:60 expected: <5> but was: <8>
[ERROR] Tests run: 12, Failures: 1, Errors: 0, Skipped: 0
```

Кодыг буцаасны дараа B8 ногоон болж, эцсийн 12 тест бүгд хамрагдсан
хэвээр байна. B8 нь мөн 42-р мөрийн F салааг бэхжүүлж хамруулна.

### 6.4 M3 — үхсэн def (dead definition)

`int total = present + best;` мөрийг нэмэхэд ямар ч тест унасангүй —
`results/mvn-test-M3.txt`-д `Tests run: 12, Failures: 0`, `BUILD SUCCESS`.
Учир нь `total`-ийг хаана ч уншдаггүй тул түүний утга программын ажиллагаанд
нөлөөлөхгүй; ямар ч оролт/гаралтын харьцуулалт үүнийг илрүүлж чадахгүй.
`javac` ч үүнийг анхааруулдаггүй (M3-ийн `mvn test` логт compiler warning
байхгүй).

Үүнийг **статик шинжилгээний багаж** л барьдаг. PMD 6.55.0
(`category/java/bestpractices.xml`) ажиллуулахад 49-р мөрөнд:

```
<violation beginline="49" endline="49" begincolumn="13" endcolumn="17"
           rule="UnusedLocalVariable" ruleset="Best Practices"
           package="mn.edu.must.sqat" class="DataFlowTargets"
           method="bonus" variable="total" priority="3">
Avoid unused local variables such as 'total'.
</violation>
```

Бүрэн гаралт `results/pmd-M3.txt`. IDE-ийн ижил inspection нь
"Variable 'total' is never used" гэж анхааруулдаг. Өөрөөр хэлбэл үхсэн
def-ийг динамик тест биш, статик анализ (IDE/PMD) илрүүлнэ.

### 6.5 Эцсийн төлөв

Бүх мутацийг буцаасны дараа `git diff 1b326ff -- src/main` хоосон
(0 мөр) — `DataFlowTargets.java` starter-тай яг ижил. Эцсийн
`mvn verify` (`results/mvn-verify-final.txt`):

```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

`results/jacoco-final.csv`-д `BRANCH_MISSED = 0`, `LINE_MISSED = 1`
(далд constructor) — шаардлагын дагуу.

---

## 7. Дүгнэлт

DU анализ нь branch хамралтаас илүү нарийн: branch coverage зөвхөн аль
салаа ажилласныг хэмждэг бол DU хосууд нь аль тодорхойлолт аль уншилтад
ямар замтайгаар хүрснийг шаарддаг тул давталтын 2 дахь удаагийн
хосуудыг (gcd-ийн (17,16), (18,16)) албадан шалгуулдаг. Энэ лабын
гол баталгаа нь gcd-ийн жишээ: зөвхөн `gcd(7,0)` ба `gcd(0,5)` тестээр
branch 100% болдог ч 4 DU хос хамрагдахгүй үлддэг. All-defs нь хямд
шат — bonus-д ердөө 2 тест хангалттай байсан ч branch 12/16, line 22/24
байсан; all-DU-pairs хүрэхэд 7 тест, branch 16/16, line 23/24 болсон.
Хамгийн их цаг авсан алхам нь B3-ын DU хүснэгт: 23 хос бүрийн def-clear
замыг гараар шалгаж, kill хийгддэг def-үүдийг (40, 41, 43, 46, 51, 54)
ялгах, мөн 42-р мөрийн F салаа мэтийн биелэшгүй салааг үндэслэх
ажил байв. Fault seeding нь DU тестийн үнэ цэнийг харуулсан: M1 шууд
баригдсан, M2 нь `present == streak` давхцлаас болж 11 тестийг давсан
боловч `present > streak` болох B8 тестээр баригдсан. M3 (үхсэн def)
нь динамик тестээр огт баригдахгүй — зөвхөн статик анализ (PMD,
IDE inspection) илрүүлсэн. Иймээс DU анализ нь тестийн чанарыг
branch хамралтаас илүү нарийвчлан хэмждэг ч гэсэн үхсэн def, утгын
алдаа, зааг утгын алдаа зэргийг дангаараа барихгүй тул статик анализ
болон утгын тесттэй хослуулах шаардлагатай. Branch 100% ба DU 100%
аль аль нь "бүх алдааг барьсан" гэсэн баталгаа биш гэдгийг M2 харуулж
байна.

## 8. AI ашигласан тухай

Энэ ажлыг бэлтгэхдээ AI туслах (OpenCode) ашигласан: DU хүснэгтийн
ноорог, JUnit тестийн классын бүтэц, CFG-ийн Graphviz эх файл,
README-ийн бүтцийг гаргахад туслалцаа авсан. AI-ийн санал болгосон
хосуудыг шууд хуулаагүй — мөр бүрийн def/use-ийг `DataFlowTargets.java`-той
тулгаж, def-clear зам, kill, биелэшгүй салааг өөрөө дахин шалгасан.
README-д бичсэн бүх тоо (`results/*.txt`, `results/*.csv`) нь локал
орчинд бодитоор ажиллуулсан Maven/JaCoCo/PMD гаралтаас авсан; гараар
зассан, таамагласан тоо, хуурамч `Total time`/`Finished at` байхгүй.
AI-ийн өгсөн заавар, бөглөөгүй загварын орлуулга (`[Таны нэр]`, `<...>`)
README-д үлдээгээгүй.

## 9. Ажлын явц (commit-ийн түүх)

Ажлын үе шат бүрийг дуусмагц нь тусдаа commit хийсэн; бүх файлыг нэг
дор оруулаагүй. Commit-ууд нь бодит ажлын явцад, 10:05–10:17 хооронд
үе шаттайгаар үүссэн (гараар зассан, бүхэл минутласан цаг байхгүй):

| # | Огноо, цаг (+08) | Hash | Commit |
|---|---|---|---|
| 1 | 2026-11-17 10:05 | `1b326ff` | Starter: lab10-dataflow project (DataFlowTargets unchanged) + starter verify output |
| 2 | 2026-11-17 10:05 | `704da88` | Section A: gcd all-DU-pairs tests (4 tests, all 11 pairs) + verify output |
| 3 | 2026-11-17 10:06 | `bca0e10` | Section B4 step 1: minimal all-defs bonus suite (2 tests) + jacoco-alldefs (branch 12/16) |
| 4 | 2026-11-17 10:07 | `be79bb1` | Section B4 step 2: all-DU-pairs bonus tests (7 tests, 23/23 pairs, branch 16/16) + jacoco-alldupairs |
| 5 | 2026-11-17 10:10 | `f5fd0e8` | Section B1: CFG for bonus() with file line numbers and per-edge test method names |
| 6 | 2026-11-17 10:10 | `fbb5324` | Section C M1: remove streak update -> 2 tests fail (expected 8/3, was 5/0) |
| 7 | 2026-11-17 10:11 | `5bafe26` | Section C M2: best = present (wrong use) SURVIVES all 11 DU tests |
| 8 | 2026-11-17 10:11 | `a7d0ded` | Section C M2-kill: present=6/streak=4 test kills the bug (expected 5, was 8); src restored |
| 9 | 2026-11-17 10:12 | `86e09d0` | Section C M3: dead def total survives 12/12 tests; PMD UnusedLocalVariable (line 49); src restored |
| 10 | 2026-11-17 10:17 | `0289785` | Section C final: mvn verify green (12 tests), jacoco-final branch 16/16, line 23/24 |
| 11 | энэ commit | — | README: final report (DU tables, CFG, coverage, fault seeding, conclusion) |

`.gitignore` нь `target/`, `.idea/`, `*.iml`, `.vscode/`, `.DS_Store`-ийг
хасдаг тул build-ийн гаралт, IDE-ийн файл репод ороогүй. Багшийн `.docx`
файлыг репод оруулаагүй.
