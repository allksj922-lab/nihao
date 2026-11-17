package com.example.model

enum class PhraseCategory(
  val id: String,
  val koreanName: String,
  val chineseName: String,
  val subtitle: String,
) {
  GREETINGS("greetings", "인사·기본", "问候·基础", "첫인사, 감사, 사과, 기본 응답"),
  DINING("dining", "식당·카페", "餐厅·咖啡", "주문, 맵기 조절, 계산하기"),
  SHOPPING("shopping", "쇼핑·흥정", "购物·砍价", "가격 묻기, 사이즈, 할인 요청"),
  TRANSPORT("transport", "교통·길찾기", "交通·问路", "택시, 지하철, 위치 물어보기"),
  HOTEL("hotel", "호텔·숙소", "酒店·住宿", "체크인, 와이파이, 객실 요청"),
  TRAVEL("travel", "여행·관광", "旅游·观光", "티켓 구매, 사진 부탁, 추천 묻기"),
  BUSINESS("business", "비즈니스·직장", "商务·职场", "회의 인사, 명함, 일정 조율"),
  EMERGENCY("emergency", "긴급·병원", "紧急·医院", "도움 요청, 약국, 분실 신고"),
}

data class PhraseItem(
  val id: String,
  val category: PhraseCategory,
  val hanzi: String,
  val pinyin: String,
  val hangulHint: String,
  val koreanMeaning: String,
  val tip: String,
  val isDailyFeatured: Boolean = false,
)

enum class SpeakerRole(val label: String) {
  PARTNER("상대방 (A)"),
  LEARNER("나 (B)"),
}

data class DialogueLine(
  val id: String,
  val role: SpeakerRole,
  val speakerName: String,
  val hanzi: String,
  val pinyin: String,
  val hangulHint: String,
  val koreanMeaning: String,
)

data class DialogueScenario(
  val id: String,
  val title: String,
  val category: PhraseCategory,
  val situationDescription: String,
  val lines: List<DialogueLine>,
)

data class ToneExample(
  val toneNumber: Int,
  val name: String,
  val pitchContour: String,
  val pinyin: String,
  val hanzi: String,
  val meaning: String,
  val description: String,
)

enum class QuizMode(val title: String, val description: String) {
  LISTEN_AND_MATCH("듣고 뜻 맞추기", "중국어 발음을 듣고 알맞은 한국어 뜻을 고르세요"),
  MEANING_TO_CHINESE("뜻 보고 중국어 고르기", "한국어 문장을 보고 알맞은 중국어 표현을 고르세요"),
}

data class QuizQuestion(
  val promptPhrase: PhraseItem,
  val options: List<PhraseItem>,
  val correctIndex: Int,
)
