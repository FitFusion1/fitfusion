package com.fitfusion.enums;

public enum Muscle {
    // 머리 / 목
    STERNOCLEIDOMASTOID("흉쇄유돌근"),

    // 어깨
    DELTOID_ANTERIOR("전면 삼각근"),
    DELTOID_LATERAL("측면 삼각근"),
    DELTOID_POSTERIOR("후면 삼각근"),
    ROTATOR_CUFF("회전근개"),

    // 가슴
    PECTORALIS_MAJOR("대흉근"),
    PECTORALIS_MINOR("소흉근"),

    // 팔 - 이두, 삼두
    BICEPS_BRACHII("상완이두근"),
    BRACHIALIS("상완근"),
    BRACHIORADIALIS("완요골근"),
    TRICEPS_BRACHII("상완삼두근"),

    // 팔 - 전완
    FLEXOR_GROUP("전완 굴곡근"),
    EXTENSOR_GROUP("전완 신전근"),

    // 복부
    RECTUS_ABDOMINIS("복직근"),
    EXTERNAL_OBLIQUE("외복사근"),
    INTERNAL_OBLIQUE("내복사근"),
    TRANSVERSUS_ABDOMINIS("복횡근"),

    // 등
    TRAPEZIUS("승모근"),
    LATISSIMUS_DORSI("광배근"),
    RHOMBOIDS("능형근"),
    ERECTOR_SPINAE("척추기립근"),
    TERES_MAJOR("대원근"),
    TERES_MINOR("소원근"),
    INFRASPINATUS("극하근"),
    SUPRASPINATUS("극상근"),

    // 엉덩이
    GLUTEUS_MAXIMUS("대둔근"),
    GLUTEUS_MEDIUS("중둔근"),
    GLUTEUS_MINIMUS("소둔근"),
    PIRIFORMIS("이상근"),

    // 허벅지 - 앞쪽
    QUADRICEPS("대퇴사두근"),
    RECTUS_FEMORIS("대퇴직근"),
    VASTUS_LATERALIS("외측광근"),
    VASTUS_MEDIALIS("내측광근"),
    VASTUS_INTERMEDIUS("중간광근"),

    // 허벅지 - 뒤쪽
    HAMSTRINGS("햄스트링"),
    BICEPS_FEMORIS("대퇴이두근"),
    SEMITENDINOSUS("반건양근"),
    SEMIMEMBRANOSUS("반막양근"),

    // 허벅지 - 내측
    ADDUCTOR_MAGNUS("대내전근"),
    ADDUCTOR_LONGUS("장내전근"),
    ADDUCTOR_BREVIS("단내전근"),
    GRACILIS("박근"),
    PECTINEUS("치골근"),

    // 고관절 굴곡근
    ILIOPSOAS("장요근"),
    SARTORIUS("봉공근"),

    // 종아리
    GASTROCNEMIUS("비복근"),
    SOLEUS("가자미근"),
    TIBIALIS_ANTERIOR("전경골근"),
    PERONEUS_LONGUS("장비골근"),

    // 발
    FLEXOR_DIGITORUM_LONGUS("긴발가락굽힘근"),
    EXTENSOR_DIGITORUM_LONGUS("긴발가락폄근"),

    // 손
    FLEXOR_DIGITORUM("손가락 굴곡근"),
    EXTENSOR_DIGITORUM("손가락 신전근");

    private final String mucleName;

    Muscle(String mucleName) {
        this.mucleName = mucleName;
    }

    public String getMucleName() {
        return mucleName;
    }
}
