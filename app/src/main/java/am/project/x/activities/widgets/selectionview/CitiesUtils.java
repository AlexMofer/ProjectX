package am.project.x.activities.widgets.selectionview;

import java.util.ArrayList;

/**
 * 省份、城市工具类
 *
 * @author Mofer
 */
public class CitiesUtils {

    /**
     * 省份枚举
     *
     * @author Mofer
     */
    public enum Province {
        BEI_JING(11, "\u5317\u4EAC\u5E02"),
        TIAN_JIN(12, "\u5929\u6D25\u5E02"),
        SHANG_HAI(31, "\u4E0A\u6D77\u5E02"),
        CHONG_QING(50, "\u91CD\u5E86\u5E02"),
        XIANG_GANG(91, "\u9999\u6E2F"),
        AO_MEN(92, "\u6FB3\u95E8"),
        TAI_WAN(71, "\u53F0\u6E7E"),
        HE_BEI(13, "\u6CB3\u5317\u7701"),
        SHAN_XI_1(14, "\u5C71\u897F\u7701"),
        NEI_MENG_GU(15, "\u5185\u8499\u53E4"),
        LIAO_NING(21, "\u8FBD\u5B81\u7701"),
        JI_LIN(22, "\u5409\u6797\u7701"),
        HEI_LONG_JIANG(23, "\u9ED1\u9F99\u6C5F"),
        JIANG_SU(32, "\u6C5F\u82CF\u7701"),
        ZHE_JIANG(33, "\u6D59\u6C5F\u7701"),
        AN_HUI(34, "\u5B89\u5FBD\u7701"),
        FU_JIAN(35, "\u798F\u5EFA\u7701"),
        JIANG_XI(36, "\u6C5F\u897F\u7701"),
        SHAN_DONG(37, "\u5C71\u4E1C\u7701"),
        HE_NAN(41, "\u6CB3\u5357\u7701"),
        HU_BEI(42, "\u6E56\u5317\u7701"),
        HU_NAN(43, "\u6E56\u5357\u7701"),
        GUANG_DONG(44, "\u5E7F\u4E1C\u7701"),
        GUANG_XI(45, "\u5E7F\u897F"),
        HAI_NAN(46, "\u6D77\u5357\u7701"),
        SI_CHUAN(51, "\u56DB\u5DDD\u7701"),
        GUI_ZHOU(52, "\u8D35\u5DDE\u7701"),
        YUN_NAN(53, "\u4E91\u5357\u7701"),
        XI_ZANG(54, "\u897F\u85CF"),
        SHAN_XI_3(61, "\u9655\u897F\u7701"),
        GAN_SU(62, "\u7518\u8083\u7701"),
        QING_HAI(63, "\u9752\u6D77\u7701"),
        NING_XIA(64, "\u5B81\u590F"),
        XIN_JIANG(65, "\u65B0\u7586");
        private int id;
        private String name;

        private Province(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isDirectControlled() {
            switch (this) {
                case BEI_JING:
                case TIAN_JIN:
                case SHANG_HAI:
                case CHONG_QING:
                case XIANG_GANG:
                case AO_MEN:
                case TAI_WAN:
                    return true;
                default:
                    return false;
            }
        }

        public static Province getProvince(int id) {
            for (Province province : Province.values()) {
                if (province.getId() == id) {
                    return province;
                }
            }
            return null;
        }
    }

    /**
     * 城市枚举
     *
     * @author Mofer
     */
    public enum City {
        BEI_JING(110, "北京市", Province.BEI_JING), TIAN_JIN(122, "天津市",
                Province.TIAN_JIN), SHANG_HAI(121, "上海市", Province.SHANG_HAI), CHONG_QING(
                123, "重庆市", Province.CHONG_QING), XIANG_GANG(1001, "香港",
                Province.XIANG_GANG), AO_MEN(1002, "澳门", Province.AO_MEN), TAI_WAN(
                1003, "台湾", Province.TAI_WAN), HAN_DAN(310, "邯郸市",
                Province.HE_BEI), SHI_JIA_ZHUANG(311, "石家庄", Province.HE_BEI), BAO_DING(
                312, "保定市", Province.HE_BEI), ZHANG_JIA_KOU(313, "张家口",
                Province.HE_BEI), CHENG_DE(314, "承德市", Province.HE_BEI), TANG_SHAN(
                315, "唐山市", Province.HE_BEI), LANG_FANG(316, "廊坊市",
                Province.HE_BEI), CANG_ZHOU(317, "沧州市", Province.HE_BEI), HENG_SHUI(
                318, "衡水市", Province.HE_BEI), XING_TAI(319, "邢台市",
                Province.HE_BEI), QIN_HUANG_DAO(335, "秦皇岛", Province.HE_BEI), SHUO_ZHOU(
                349, "朔州市", Province.SHAN_XI_1), XIN_ZHOU(350, "忻州市",
                Province.SHAN_XI_1), TAI_YUAN(351, "太原市", Province.SHAN_XI_1), DA_TONG(
                352, "大同市", Province.SHAN_XI_1), YANG_QUAN(353, "阳泉市",
                Province.SHAN_XI_1), JIN_ZHONG(354, "晋中市", Province.SHAN_XI_1), CHANG_ZHI(
                355, "长治市", Province.SHAN_XI_1), JIN_CHENG(356, "晋城市",
                Province.SHAN_XI_1), LIN_FEN(357, "临汾市", Province.SHAN_XI_1), LV_LIANG(
                358, "吕梁市", Province.SHAN_XI_1), YUN_CHENG(359, "运城市",
                Province.SHAN_XI_1), HU_LUN_BEI_ER(470, "呼伦贝尔",
                Province.NEI_MENG_GU), HU_HE_HAO_TE(471, "呼和浩特",
                Province.NEI_MENG_GU), BAO_TOU(472, "包头市", Province.NEI_MENG_GU), WU_HAI(
                473, "乌海市", Province.NEI_MENG_GU), WU_LAN_CHA_BU(474, "乌兰察布",
                Province.NEI_MENG_GU), TONG_LIAO(475, "通辽市",
                Province.NEI_MENG_GU), CHI_FENG(476, "赤峰市",
                Province.NEI_MENG_GU), E_ER_DUO_SI(477, "鄂尔多斯",
                Province.NEI_MENG_GU), BA_YAN_NAO_ER(478, "巴彦淖尔",
                Province.NEI_MENG_GU), XI_LIN_GUO_LE_MENG(479, "锡林郭勒盟",
                Province.NEI_MENG_GU), XING_AN_MENG(482, "兴安盟",
                Province.NEI_MENG_GU), A_LA_SHAN_MENG(483, "阿拉善盟",
                Province.NEI_MENG_GU), SHEN_YANG(124, "沈阳市", Province.LIAO_NING), DA_LIAN(
                411, "大连市", Province.LIAO_NING), AN_SHAN(412, "鞍山市",
                Province.LIAO_NING), FU_SHUN(413, "抚顺市", Province.LIAO_NING), BEN_XI(
                414, "本溪市", Province.LIAO_NING), DAN_DONG(415, "丹东市",
                Province.LIAO_NING), JIN_ZHOU(416, "锦州市", Province.LIAO_NING), YING_KOU(
                417, "营口市", Province.LIAO_NING), FU_XIN(418, "阜新市",
                Province.LIAO_NING), LIAO_YANG(419, "辽阳市", Province.LIAO_NING), TIE_LING(
                420, "铁岭市", Province.LIAO_NING), CHAO_YANG(421, "朝阳市",
                Province.LIAO_NING), PAN_JIN(427, "盘锦市", Province.LIAO_NING), HU_LU_DAO(
                429, "葫芦岛", Province.LIAO_NING), CHANG_CHUN(431, "长春市",
                Province.JI_LIN), JI_LIN(432, "吉林市", Province.JI_LIN), YAN_BIAN(
                433, "延边", Province.JI_LIN), SI_PING(434, "四平市",
                Province.JI_LIN), TONG_HUA(435, "通化市", Province.JI_LIN), BAI_CHENG(
                436, "白城市", Province.JI_LIN), LIAO_YUAN(437, "辽源市",
                Province.JI_LIN), SONG_YUAN(438, "松原市", Province.JI_LIN), BAI_SHAN(
                439, "白山市", Province.JI_LIN), HA_ER_BIN(451, "哈尔滨",
                Province.HEI_LONG_JIANG), QI_QI_HA_ER(452, "齐齐哈尔",
                Province.HEI_LONG_JIANG), MU_DAN_JIANG(453, "牡丹江",
                Province.HEI_LONG_JIANG), JIA_MU_SI(454, "佳木斯",
                Province.HEI_LONG_JIANG), SUI_HUA(455, "绥化市",
                Province.HEI_LONG_JIANG), HEI_HE(456, "黑河市",
                Province.HEI_LONG_JIANG), DA_XING_AN_LING(457, "大兴安岭",
                Province.HEI_LONG_JIANG), YI_CHUN_1(458, "伊春市",
                Province.HEI_LONG_JIANG), DA_QING(459, "大庆市",
                Province.HEI_LONG_JIANG), QI_TAI_HE(464, "七台河",
                Province.HEI_LONG_JIANG), JI_XI(467, "鸡西市",
                Province.HEI_LONG_JIANG), HE_GANG(468, "鹤岗市",
                Province.HEI_LONG_JIANG), SHUANG_YA_SHAN(469, "双鸭山",
                Province.HEI_LONG_JIANG), NAN_JING(125, "南京市",
                Province.JIANG_SU), WU_XI(510, "无锡市", Province.JIANG_SU), ZHEN_JIANG(
                511, "镇江市", Province.JIANG_SU), SU_ZHOU_1(512, "苏州市",
                Province.JIANG_SU), NAN_TONG(513, "南通市", Province.JIANG_SU), YANG_ZHOU(
                514, "扬州市", Province.JIANG_SU), YAN_CHENG(515, "盐城市",
                Province.JIANG_SU), XU_ZHOU(516, "徐州市", Province.JIANG_SU), HUAI_AN(
                517, "淮安市", Province.JIANG_SU), LIAN_YUN_GANG(518, "连云港",
                Province.JIANG_SU), CHANG_ZHOU(519, "常州市", Province.JIANG_SU), TAI_ZHOU_4(
                523, "泰州市", Province.JIANG_SU), SU_QIAN(527, "宿迁市",
                Province.JIANG_SU), QU_ZHOU(570, "衢州市", Province.ZHE_JIANG), HANG_ZHOU(
                571, "杭州市", Province.ZHE_JIANG), HU_ZHOU(572, "湖州市",
                Province.ZHE_JIANG), JIA_XING(573, "嘉兴市", Province.ZHE_JIANG), NING_BO(
                574, "宁波市", Province.ZHE_JIANG), SHAO_XING(575, "绍兴市",
                Province.ZHE_JIANG), TAI_ZHOU_2(576, "台州市", Province.ZHE_JIANG), WEN_ZHOU(
                577, "温州市", Province.ZHE_JIANG), LI_SHUI(578, "丽水市",
                Province.ZHE_JIANG), JIN_HUA(579, "金华市", Province.ZHE_JIANG), ZHOU_SHAN(
                580, "舟山市", Province.ZHE_JIANG), CHU_ZHOU(550, "滁州市",
                Province.AN_HUI), HE_FEI(551, "合肥市", Province.AN_HUI), BANG_BU(
                552, "蚌埠市", Province.AN_HUI), WU_HU(553, "芜湖市", Province.AN_HUI), HUAI_NAN(
                554, "淮南市", Province.AN_HUI), MA_AN_SHAN(555, "马鞍山",
                Province.AN_HUI), AN_QING(556, "安庆市", Province.AN_HUI), SU_ZHOU_4(
                557, "宿州市", Province.AN_HUI), FU_YANG(558, "阜阳市",
                Province.AN_HUI), HUANG_SHAN(559, "黄山市", Province.AN_HUI), HUAI_BEI(
                561, "淮北市", Province.AN_HUI), TONG_LING(562, "铜陵市",
                Province.AN_HUI), XUAN_CHENG(563, "宣城市", Province.AN_HUI), LIU_AN(
                564, "六安市", Province.AN_HUI), CHI_ZHOU(566, "池州市",
                Province.AN_HUI), BO_ZHOU(567, "亳州市", Province.AN_HUI), FU_ZHOU_2(
                591, "福州市", Province.FU_JIAN), XIA_MEN(592, "厦门市",
                Province.FU_JIAN), NING_DE(593, "宁德市", Province.FU_JIAN), PU_TIAN(
                594, "莆田市", Province.FU_JIAN), QUAN_ZHOU(595, "泉州市",
                Province.FU_JIAN), ZHANG_ZHOU(596, "漳州市", Province.FU_JIAN), LONG_YAN(
                597, "龙岩市", Province.FU_JIAN), SAN_MING(598, "三明市",
                Province.FU_JIAN), NAN_PING(599, "南平市", Province.FU_JIAN), YING_TAN(
                789, "鹰潭市", Province.JIANG_XI), XIN_YU(790, "新余市",
                Province.JIANG_XI), NAN_CHANG(791, "南昌市", Province.JIANG_XI), JIU_JIANG(
                792, "九江市", Province.JIANG_XI), SHANG_RAO(793, "上饶市",
                Province.JIANG_XI), FU_ZHOU_3(794, "抚州市", Province.JIANG_XI), YI_CHUN_2(
                795, "宜春市", Province.JIANG_XI), JI_AN(796, "吉安市",
                Province.JIANG_XI), GAN_ZHOU(797, "赣州市", Province.JIANG_XI), JING_DE_ZHEN(
                798, "景德镇", Province.JIANG_XI), PING_XIANG(799, "萍乡市",
                Province.JIANG_XI), HE_ZE(530, "菏泽市", Province.SHAN_DONG), JI_NAN(
                531, "济南市", Province.SHAN_DONG), QING_DAO(532, "青岛市",
                Province.SHAN_DONG), ZI_BO(533, "淄博市", Province.SHAN_DONG), DE_ZHOU(
                534, "德州市", Province.SHAN_DONG), YAN_TAI(535, "烟台市",
                Province.SHAN_DONG), WEI_FANG(536, "潍坊市", Province.SHAN_DONG), JI_NING(
                537, "济宁市", Province.SHAN_DONG), TAI_AN(538, "泰安市",
                Province.SHAN_DONG), LIN_YI(539, "临沂市", Province.SHAN_DONG), BIN_ZHOU(
                543, "滨州市", Province.SHAN_DONG), DONG_YING(546, "东营市",
                Province.SHAN_DONG), WEI_HAI(631, "威海市", Province.SHAN_DONG), ZAO_ZHUANG(
                632, "枣庄市", Province.SHAN_DONG), RI_ZHAO(633, "日照市",
                Province.SHAN_DONG), LAI_WU(634, "莱芜市", Province.SHAN_DONG), LIAO_CHENG(
                635, "聊城市", Province.SHAN_DONG), SHANG_QIU(370, "商丘市",
                Province.HE_NAN), ZHENG_ZHOU(371, "郑州市", Province.HE_NAN), AN_YANG(
                372, "安阳市", Province.HE_NAN), XIN_XIANG(373, "新乡市",
                Province.HE_NAN), XU_CHANG(374, "许昌市", Province.HE_NAN), PING_DING_SHAN(
                375, "平顶山", Province.HE_NAN), XIN_YANG(376, "信阳市",
                Province.HE_NAN), NAN_YANG(377, "南阳市", Province.HE_NAN), KAI_FENG(
                378, "开封市", Province.HE_NAN), LUO_YANG(379, "洛阳市",
                Province.HE_NAN), JIAO_ZUO(391, "焦作市", Province.HE_NAN), HE_BI(
                392, "鹤壁市", Province.HE_NAN), PU_YANG(393, "濮阳市",
                Province.HE_NAN), ZHOU_KOU(394, "周口市", Province.HE_NAN), LUO_HE(
                395, "漯河市", Province.HE_NAN), ZHU_MA_DIAN(396, "驻马店",
                Province.HE_NAN), SAN_MEN_XIA(398, "三门峡", Province.HE_NAN), WU_HAN(
                127, "武汉市", Province.HU_BEI), XIANG_YANG(710, "襄阳市",
                Province.HU_BEI), E_ZHOU(711, "鄂州市", Province.HU_BEI), XIAO_GAN(
                712, "孝感市", Province.HU_BEI), HUANG_GANG(713, "黄冈市",
                Province.HU_BEI), HUANG_SHI(714, "黄石市", Province.HU_BEI), XIAN_NING(
                715, "咸宁市", Province.HU_BEI), JING_ZHOU(716, "荆州市",
                Province.HU_BEI), YI_CHANG(717, "宜昌市", Province.HU_BEI), EN_SHI(
                718, "恩施", Province.HU_BEI), SHI_YAN(719, "十堰市",
                Province.HU_BEI), SUI_ZHOU(722, "随州市", Province.HU_BEI), JING_MEN(
                724, "荆门市", Province.HU_BEI), YUE_YANG(730, "岳阳市",
                Province.HU_NAN), CHANG_SHA(731, "长沙市", Province.HU_NAN), XIANG_TAN(
                732, "湘潭市", Province.HU_NAN), ZHU_ZHOU(733, "株州市",
                Province.HU_NAN), HENG_YANG(734, "衡阳市", Province.HU_NAN), CHEN_ZHOU(
                735, "郴州市", Province.HU_NAN), CHANG_DE(736, "常德市",
                Province.HU_NAN), YI_YANG(737, "益阳市", Province.HU_NAN), LOU_DI(
                738, "娄底市", Province.HU_NAN), SHAO_YANG(739, "邵阳市",
                Province.HU_NAN), XIANG_XI(743, "湘西", Province.HU_NAN), ZHANG_JIA_JIE(
                744, "张家界", Province.HU_NAN), HUAI_HUA(745, "怀化市",
                Province.HU_NAN), YONG_ZHOU(746, "永州市", Province.HU_NAN), GUANG_ZHOU(
                120, "广州市", Province.GUANG_DONG), SHAN_WEI(660, "汕尾市",
                Province.GUANG_DONG), YANG_JIANG(662, "阳江市",
                Province.GUANG_DONG), JIE_YANG(663, "揭阳市", Province.GUANG_DONG), MAO_MING(
                668, "茂名市", Province.GUANG_DONG), JIANG_MEN(750, "江门市",
                Province.GUANG_DONG), SHAO_GUAN(751, "韶关市", Province.GUANG_DONG), HUI_ZHOU(
                752, "惠州市", Province.GUANG_DONG), MEI_ZHOU(753, "梅州市",
                Province.GUANG_DONG), SHAN_TOU(754, "汕头市", Province.GUANG_DONG), SHEN_ZHEN(
                755, "深圳市", Province.GUANG_DONG), ZHU_HAI(756, "珠海市",
                Province.GUANG_DONG), FO_SHAN(757, "佛山市", Province.GUANG_DONG), ZHAO_QING(
                758, "肇庆市", Province.GUANG_DONG), ZHAN_JIANG(759, "湛江市",
                Province.GUANG_DONG), ZHONG_SHAN(760, "中山市",
                Province.GUANG_DONG), HE_YUAN(762, "河源市", Province.GUANG_DONG), QING_YUAN(
                763, "清远市", Province.GUANG_DONG), YUN_FU(766, "云浮市",
                Province.GUANG_DONG), CHAO_ZHOU(768, "潮州市", Province.GUANG_DONG), DONG_GUAN(
                769, "东莞市", Province.GUANG_DONG), FANG_CHENG_GANG(770, "防城港",
                Province.GUANG_XI), NAN_NING(771, "南宁市", Province.GUANG_XI), LIU_ZHOU(
                772, "柳州市", Province.GUANG_XI), GUI_LIN(773, "桂林市",
                Province.GUANG_XI), WU_ZHOU(774, "梧州市", Province.GUANG_XI), YU_LIN_4(
                775, "玉林市", Province.GUANG_XI), BAI_SE(776, "百色市",
                Province.GUANG_XI), QIN_ZHOU(777, "钦州市", Province.GUANG_XI), HE_CHI(
                778, "河池市", Province.GUANG_XI), BEI_HAI(779, "北海市",
                Province.GUANG_XI), CHONG_ZUO(780, "崇左市", Province.GUANG_XI), LAI_BIN(
                781, "来宾市", Province.GUANG_XI), HE_ZHOU(782, "贺州市",
                Province.GUANG_XI), GUI_GANG(783, "贵港市", Province.GUANG_XI), HAI_KOU(
                898, "海口市", Province.HAI_NAN), SAN_YA(899, "三亚市",
                Province.HAI_NAN), SAN_SHA(900, "三沙市", Province.HAI_NAN), CHENG_DOU(
                128, "成都市", Province.SI_CHUAN), PAN_ZHI_HUA(812, "攀枝花",
                Province.SI_CHUAN), ZI_GONG(813, "自贡市", Province.SI_CHUAN), ZI_YANG(
                814, "资阳市", Province.SI_CHUAN), MEI_SHAN(815, "眉山市",
                Province.SI_CHUAN), MIAN_YANG(816, "绵阳市", Province.SI_CHUAN), NAN_CHONG(
                817, "南充市", Province.SI_CHUAN), DA_ZHOU(818, "达州市",
                Province.SI_CHUAN), SUI_NING(825, "遂宁市", Province.SI_CHUAN), GUANG_AN(
                826, "广安市", Province.SI_CHUAN), BA_ZHONG(827, "巴中市",
                Province.SI_CHUAN), LU_ZHOU(830, "泸州市", Province.SI_CHUAN), YI_BIN(
                831, "宜宾市", Province.SI_CHUAN), NEI_JIANG(832, "内江市",
                Province.SI_CHUAN), LE_SHAN(833, "乐山市", Province.SI_CHUAN), LIANG_SHAN(
                834, "凉山", Province.SI_CHUAN), YA_AN(835, "雅安市",
                Province.SI_CHUAN), GAN_ZI(836, "甘孜", Province.SI_CHUAN), A_BA(
                837, "阿坝", Province.SI_CHUAN), DE_YANG(838, "德阳市",
                Province.SI_CHUAN), GUANG_YUAN(839, "广元市", Province.SI_CHUAN), GUI_YANG(
                851, "贵阳市", Province.GUI_ZHOU), ZUN_YI(852, "遵义市",
                Province.GUI_ZHOU), AN_SHUN(853, "安顺市", Province.GUI_ZHOU), QIAN_NAN(
                854, "黔南", Province.GUI_ZHOU), QIAN_DONG_NAN(855, "黔东南",
                Province.GUI_ZHOU), TONG_REN(856, "铜仁市", Province.GUI_ZHOU), BI_JIE(
                857, "毕节市", Province.GUI_ZHOU), LIU_PAN_SHUI(858, "六盘水",
                Province.GUI_ZHOU), QIAN_XI_NAN(859, "黔西南", Province.GUI_ZHOU), DE_HONG(
                692, "德宏", Province.YUN_NAN), ZHAO_TONG(870, "昭通市",
                Province.YUN_NAN), KUN_MING(871, "昆明市", Province.YUN_NAN), DA_LI(
                872, "大理", Province.YUN_NAN), HONG_HE(873, "红河",
                Province.YUN_NAN), QU_JING(874, "曲靖市", Province.YUN_NAN), BAO_SHAN(
                875, "保山市", Province.YUN_NAN), WEN_SHAN(876, "文山",
                Province.YUN_NAN), YU_XI(877, "玉溪市", Province.YUN_NAN), CHU_XIONG(
                878, "楚雄", Province.YUN_NAN), PU_ER(879, "普洱市",
                Province.YUN_NAN), XI_SHUANG_BAN_NA(880, "西双版纳",
                Province.YUN_NAN), LIN_CANG(883, "临沧市", Province.YUN_NAN), NU_JIANG(
                886, "怒江", Province.YUN_NAN), DI_QING(887, "迪庆",
                Province.YUN_NAN), LI_JIANG(888, "丽江市", Province.YUN_NAN), LA_SA(
                891, "拉萨市", Province.XI_ZANG), RI_KA_ZE(892, "日喀则",
                Province.XI_ZANG), SHAN_NAN(893, "山南", Province.XI_ZANG), LIN_ZHI(
                894, "林芝", Province.XI_ZANG), CHANG_DOU(895, "昌都市",
                Province.XI_ZANG), NA_QU(896, "那曲", Province.XI_ZANG), A_LI(
                897, "阿里", Province.XI_ZANG), XI_AN(129, "西安市",
                Province.SHAN_XI_3), XIAN_YANG(910, "咸阳市", Province.SHAN_XI_3), YAN_AN(
                911, "延安市", Province.SHAN_XI_3), YU_LIN_2(912, "榆林市",
                Province.SHAN_XI_3), WEI_NAN(913, "渭南市", Province.SHAN_XI_3), SHANG_LUO(
                914, "商洛市", Province.SHAN_XI_3), AN_KANG(915, "安康市",
                Province.SHAN_XI_3), HAN_ZHONG(916, "汉中市", Province.SHAN_XI_3), BAO_JI(
                917, "宝鸡市", Province.SHAN_XI_3), TONG_CHUAN(919, "铜川市",
                Province.SHAN_XI_3), LIN_XIA(930, "临夏", Province.GAN_SU), LAN_ZHOU(
                931, "兰州市", Province.GAN_SU), DING_XI(932, "定西市",
                Province.GAN_SU), PING_LIANG(933, "平凉市", Province.GAN_SU), QING_YANG(
                934, "庆阳市", Province.GAN_SU), WU_WEI(935, "武威市",
                Province.GAN_SU), ZHANG_YE(936, "张掖市", Province.GAN_SU), JIU_QUAN(
                937, "酒泉市", Province.GAN_SU), TIAN_SHUI(938, "天水市",
                Province.GAN_SU), JIA_YU_GUAN(939, "嘉峪关", Province.GAN_SU), JIN_CHANG(
                940, "金昌市", Province.GAN_SU), GAN_NAN(941, "甘南",
                Province.GAN_SU), LONG_NAN(942, "陇南市", Province.GAN_SU), BAI_YIN(
                943, "白银市", Province.GAN_SU), HAI_BEI(970, "海北",
                Province.QING_HAI), XI_NING(971, "西宁市", Province.QING_HAI), HAI_DONG(
                972, "海东市", Province.QING_HAI), HUANG_NAN(973, "黄南",
                Province.QING_HAI), HAI_NAN(974, "海南", Province.QING_HAI), GUO_LUO(
                975, "果洛", Province.QING_HAI), YU_SHU(976, "玉树市",
                Province.QING_HAI), HAI_XI(977, "海西", Province.QING_HAI), YIN_CHUAN(
                951, "银川市", Province.NING_XIA), SHI_ZUI_SHAN(952, "石嘴山",
                Province.NING_XIA), WU_ZHONG(953, "吴忠市", Province.NING_XIA), GU_YUAN(
                954, "固原市", Province.NING_XIA), ZHONG_WEI(955, "中卫市",
                Province.NING_XIA), HA_MI(902, "哈密", Province.XIN_JIANG), HE_TIAN(
                903, "和田", Province.XIN_JIANG), A_LE_TAI(906, "阿勒泰",
                Province.XIN_JIANG), KE_ZI_LE_SU_KE_ER_KE_ZI(908, "克孜勒苏柯尔克孜",
                Province.XIN_JIANG), BO_ER_TA_LA(909, "博尔塔拉",
                Province.XIN_JIANG), KE_LA_MA_YI(990, "克拉玛依",
                Province.XIN_JIANG), WU_LU_MU_QI(991, "乌鲁木齐",
                Province.XIN_JIANG), TA_CHENG(993, "塔城", Province.XIN_JIANG), CHANG_JI(
                994, "昌吉", Province.XIN_JIANG), TU_LU_FAN(995, "吐鲁番",
                Province.XIN_JIANG), BA_YIN_GUO_LENG(996, "巴音郭楞",
                Province.XIN_JIANG), A_KE_SU(997, "阿克苏", Province.XIN_JIANG), KA_SHI(
                998, "喀什", Province.XIN_JIANG), YI_LI_HA_SA_KE(999, "伊犁哈萨克",
                Province.XIN_JIANG);

        private int id;
        private String name;
        private Province province;

        City(int id, String name, Province province) {
            this.id = id;
            this.name = name;
            this.province = province;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Province getProvince() {
            return province;
        }

        public boolean isDirectControlled() {
            switch (this) {
                case BEI_JING:
                case TIAN_JIN:
                case SHANG_HAI:
                case CHONG_QING:
                case XIANG_GANG:
                case AO_MEN:
                case TAI_WAN:
                    return true;
                default:
                    return false;
            }
        }

        public static City getCity(int id) {
            for (City city : City.values()) {
                if (city.getId() == id) {
                    return city;
                }
            }
            return null;
        }

    }

    /**
     * 省份实体
     *
     * @author Mofer
     */
    public static class ProvinceDto {
        private Province province;

        public ProvinceDto() {
        }

        public ProvinceDto(Province province) {
            this.province = province;
        }

        public ProvinceDto(int id) {
            this.province = Province.getProvince(id);
        }

        public int getProvinceId() {
            return province == null ? 0 : province.getId();
        }

        public String getProvinceName() {
            return province == null ? null : province.getName();
        }

        public void setProvince(Province province) {
            this.province = province;
        }

        public Province getProvince() {
            return province;
        }

        public boolean isDirectControlled() {
            return province != null && province.isDirectControlled();
        }
    }

    /**
     * 城市实体
     *
     * @author Mofer
     */
    public static class CityDto extends ProvinceDto {

        private City city;

        public CityDto() {
        }

        public CityDto(int id) {
            this(City.getCity(id));
        }

        public CityDto(City city) {
            super(city == null ? null : city.getProvince());
            this.city = city;
        }

        public int getCityId() {
            return city == null ? 0 : city.getId();
        }

        public String getCityName() {
            return city == null ? null : city.getName();
        }

        public boolean isDirectControlled() {
            return city != null && city.isDirectControlled();
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            if (city != null) {
                setProvince(city.getProvince());
            }
            this.city = city;
        }
    }

    /**
     * 获取省份集合（拼音排序）
     *
     * @return
     */
    public static ArrayList<ProvinceDto> getProvinceList() {
        ArrayList<ProvinceDto> provinces = new ArrayList<ProvinceDto>();
        provinces.add(new ProvinceDto(Province.AN_HUI));
        provinces.add(new ProvinceDto(Province.AO_MEN));
        provinces.add(new ProvinceDto(Province.BEI_JING));
        provinces.add(new ProvinceDto(Province.CHONG_QING));
        provinces.add(new ProvinceDto(Province.FU_JIAN));
        provinces.add(new ProvinceDto(Province.GAN_SU));
        provinces.add(new ProvinceDto(Province.GUANG_DONG));
        provinces.add(new ProvinceDto(Province.GUANG_XI));
        provinces.add(new ProvinceDto(Province.GUI_ZHOU));
        provinces.add(new ProvinceDto(Province.HAI_NAN));
        provinces.add(new ProvinceDto(Province.HE_BEI));
        provinces.add(new ProvinceDto(Province.HE_NAN));
        provinces.add(new ProvinceDto(Province.HEI_LONG_JIANG));
        provinces.add(new ProvinceDto(Province.HU_BEI));
        provinces.add(new ProvinceDto(Province.HU_NAN));
        provinces.add(new ProvinceDto(Province.JI_LIN));
        provinces.add(new ProvinceDto(Province.JIANG_SU));
        provinces.add(new ProvinceDto(Province.JIANG_XI));
        provinces.add(new ProvinceDto(Province.LIAO_NING));
        provinces.add(new ProvinceDto(Province.NEI_MENG_GU));
        provinces.add(new ProvinceDto(Province.NING_XIA));
        provinces.add(new ProvinceDto(Province.QING_HAI));
        provinces.add(new ProvinceDto(Province.SHAN_DONG));
        provinces.add(new ProvinceDto(Province.SHAN_XI_1));
        provinces.add(new ProvinceDto(Province.SHAN_XI_3));
        provinces.add(new ProvinceDto(Province.SHANG_HAI));
        provinces.add(new ProvinceDto(Province.SI_CHUAN));
        provinces.add(new ProvinceDto(Province.TAI_WAN));
        provinces.add(new ProvinceDto(Province.TIAN_JIN));
        provinces.add(new ProvinceDto(Province.XI_ZANG));
        provinces.add(new ProvinceDto(Province.XIANG_GANG));
        provinces.add(new ProvinceDto(Province.XIN_JIANG));
        provinces.add(new ProvinceDto(Province.YUN_NAN));
        provinces.add(new ProvinceDto(Province.ZHE_JIANG));
        return provinces;
    }

    /**
     * 获取城市合集（拼音排序）
     *
     * @return
     */
    public static ArrayList<CityDto> getCityList() {
        ArrayList<CityDto> citys = new ArrayList<CityDto>();
        citys.add(new CityDto(City.A_BA));
        citys.add(new CityDto(City.A_KE_SU));
        citys.add(new CityDto(City.A_LA_SHAN_MENG));
        citys.add(new CityDto(City.A_LE_TAI));
        citys.add(new CityDto(City.A_LI));
        citys.add(new CityDto(City.AN_KANG));
        citys.add(new CityDto(City.AN_QING));
        citys.add(new CityDto(City.AN_SHAN));
        citys.add(new CityDto(City.AN_SHUN));
        citys.add(new CityDto(City.AN_YANG));
        citys.add(new CityDto(City.AO_MEN));
        citys.add(new CityDto(City.BA_YAN_NAO_ER));
        citys.add(new CityDto(City.BA_YIN_GUO_LENG));
        citys.add(new CityDto(City.BA_ZHONG));
        citys.add(new CityDto(City.BAI_CHENG));
        citys.add(new CityDto(City.BAI_SE));
        citys.add(new CityDto(City.BAI_SHAN));
        citys.add(new CityDto(City.BAI_YIN));
        citys.add(new CityDto(City.BANG_BU));
        citys.add(new CityDto(City.BAO_DING));
        citys.add(new CityDto(City.BAO_JI));
        citys.add(new CityDto(City.BAO_SHAN));
        citys.add(new CityDto(City.BAO_TOU));
        citys.add(new CityDto(City.BEI_HAI));
        citys.add(new CityDto(City.BEI_JING));
        citys.add(new CityDto(City.BEN_XI));
        citys.add(new CityDto(City.BI_JIE));
        citys.add(new CityDto(City.BIN_ZHOU));
        citys.add(new CityDto(City.BO_ER_TA_LA));
        citys.add(new CityDto(City.BO_ZHOU));
        citys.add(new CityDto(City.CANG_ZHOU));
        citys.add(new CityDto(City.CHANG_CHUN));
        citys.add(new CityDto(City.CHANG_DE));
        citys.add(new CityDto(City.CHANG_DOU));
        citys.add(new CityDto(City.CHANG_JI));
        citys.add(new CityDto(City.CHANG_SHA));
        citys.add(new CityDto(City.CHANG_ZHI));
        citys.add(new CityDto(City.CHANG_ZHOU));
        citys.add(new CityDto(City.CHAO_YANG));
        citys.add(new CityDto(City.CHAO_ZHOU));
        citys.add(new CityDto(City.CHEN_ZHOU));
        citys.add(new CityDto(City.CHENG_DE));
        citys.add(new CityDto(City.CHENG_DOU));
        citys.add(new CityDto(City.CHI_FENG));
        citys.add(new CityDto(City.CHI_ZHOU));
        citys.add(new CityDto(City.CHONG_QING));
        citys.add(new CityDto(City.CHONG_ZUO));
        citys.add(new CityDto(City.CHU_XIONG));
        citys.add(new CityDto(City.CHU_ZHOU));
        citys.add(new CityDto(City.DA_LI));
        citys.add(new CityDto(City.DA_LIAN));
        citys.add(new CityDto(City.DA_QING));
        citys.add(new CityDto(City.DA_TONG));
        citys.add(new CityDto(City.DA_XING_AN_LING));
        citys.add(new CityDto(City.DA_ZHOU));
        citys.add(new CityDto(City.DAN_DONG));
        citys.add(new CityDto(City.DE_HONG));
        citys.add(new CityDto(City.DE_YANG));
        citys.add(new CityDto(City.DE_ZHOU));
        citys.add(new CityDto(City.DI_QING));
        citys.add(new CityDto(City.DING_XI));
        citys.add(new CityDto(City.DONG_GUAN));
        citys.add(new CityDto(City.DONG_YING));
        citys.add(new CityDto(City.E_ER_DUO_SI));
        citys.add(new CityDto(City.E_ZHOU));
        citys.add(new CityDto(City.EN_SHI));
        citys.add(new CityDto(City.FANG_CHENG_GANG));
        citys.add(new CityDto(City.FO_SHAN));
        citys.add(new CityDto(City.FU_SHUN));
        citys.add(new CityDto(City.FU_XIN));
        citys.add(new CityDto(City.FU_YANG));
        citys.add(new CityDto(City.FU_ZHOU_2));
        citys.add(new CityDto(City.FU_ZHOU_3));
        citys.add(new CityDto(City.GAN_NAN));
        citys.add(new CityDto(City.GAN_ZHOU));
        citys.add(new CityDto(City.GAN_ZI));
        citys.add(new CityDto(City.GU_YUAN));
        citys.add(new CityDto(City.GUANG_AN));
        citys.add(new CityDto(City.GUANG_YUAN));
        citys.add(new CityDto(City.GUANG_ZHOU));
        citys.add(new CityDto(City.GUI_GANG));
        citys.add(new CityDto(City.GUI_LIN));
        citys.add(new CityDto(City.GUI_YANG));
        citys.add(new CityDto(City.GUO_LUO));
        citys.add(new CityDto(City.HA_ER_BIN));
        citys.add(new CityDto(City.HA_MI));
        citys.add(new CityDto(City.HAI_BEI));
        citys.add(new CityDto(City.HAI_DONG));
        citys.add(new CityDto(City.HAI_KOU));
        citys.add(new CityDto(City.HAI_NAN));
        citys.add(new CityDto(City.HAI_XI));
        citys.add(new CityDto(City.HAN_DAN));
        citys.add(new CityDto(City.HAN_ZHONG));
        citys.add(new CityDto(City.HANG_ZHOU));
        citys.add(new CityDto(City.HE_BI));
        citys.add(new CityDto(City.HE_CHI));
        citys.add(new CityDto(City.HE_FEI));
        citys.add(new CityDto(City.HE_GANG));
        citys.add(new CityDto(City.HE_TIAN));
        citys.add(new CityDto(City.HE_YUAN));
        citys.add(new CityDto(City.HE_ZE));
        citys.add(new CityDto(City.HE_ZHOU));
        citys.add(new CityDto(City.HEI_HE));
        citys.add(new CityDto(City.HENG_SHUI));
        citys.add(new CityDto(City.HENG_YANG));
        citys.add(new CityDto(City.HONG_HE));
        citys.add(new CityDto(City.HU_HE_HAO_TE));
        citys.add(new CityDto(City.HU_LU_DAO));
        citys.add(new CityDto(City.HU_LUN_BEI_ER));
        citys.add(new CityDto(City.HU_ZHOU));
        citys.add(new CityDto(City.HUAI_AN));
        citys.add(new CityDto(City.HUAI_BEI));
        citys.add(new CityDto(City.HUAI_HUA));
        citys.add(new CityDto(City.HUAI_NAN));
        citys.add(new CityDto(City.HUANG_GANG));
        citys.add(new CityDto(City.HUANG_NAN));
        citys.add(new CityDto(City.HUANG_SHAN));
        citys.add(new CityDto(City.HUANG_SHI));
        citys.add(new CityDto(City.HUI_ZHOU));
        citys.add(new CityDto(City.JI_AN));
        citys.add(new CityDto(City.JI_LIN));
        citys.add(new CityDto(City.JI_NAN));
        citys.add(new CityDto(City.JI_NING));
        citys.add(new CityDto(City.JI_XI));
        citys.add(new CityDto(City.JIA_MU_SI));
        citys.add(new CityDto(City.JIA_XING));
        citys.add(new CityDto(City.JIA_YU_GUAN));
        citys.add(new CityDto(City.JIANG_MEN));
        citys.add(new CityDto(City.JIAO_ZUO));
        citys.add(new CityDto(City.JIE_YANG));
        citys.add(new CityDto(City.JIN_CHANG));
        citys.add(new CityDto(City.JIN_CHENG));
        citys.add(new CityDto(City.JIN_HUA));
        citys.add(new CityDto(City.JIN_ZHONG));
        citys.add(new CityDto(City.JIN_ZHOU));
        citys.add(new CityDto(City.JING_DE_ZHEN));
        citys.add(new CityDto(City.JING_MEN));
        citys.add(new CityDto(City.JING_ZHOU));
        citys.add(new CityDto(City.JIU_JIANG));
        citys.add(new CityDto(City.JIU_QUAN));
        citys.add(new CityDto(City.KA_SHI));
        citys.add(new CityDto(City.KAI_FENG));
        citys.add(new CityDto(City.KE_LA_MA_YI));
        citys.add(new CityDto(City.KE_ZI_LE_SU_KE_ER_KE_ZI));
        citys.add(new CityDto(City.KUN_MING));
        citys.add(new CityDto(City.LA_SA));
        citys.add(new CityDto(City.LAI_BIN));
        citys.add(new CityDto(City.LAI_WU));
        citys.add(new CityDto(City.LAN_ZHOU));
        citys.add(new CityDto(City.LANG_FANG));
        citys.add(new CityDto(City.LE_SHAN));
        citys.add(new CityDto(City.LI_JIANG));
        citys.add(new CityDto(City.LI_SHUI));
        citys.add(new CityDto(City.LIAN_YUN_GANG));
        citys.add(new CityDto(City.LIANG_SHAN));
        citys.add(new CityDto(City.LIAO_CHENG));
        citys.add(new CityDto(City.LIAO_YANG));
        citys.add(new CityDto(City.LIAO_YUAN));
        citys.add(new CityDto(City.LIN_CANG));
        citys.add(new CityDto(City.LIN_FEN));
        citys.add(new CityDto(City.LIN_XIA));
        citys.add(new CityDto(City.LIN_YI));
        citys.add(new CityDto(City.LIN_ZHI));
        citys.add(new CityDto(City.LIU_AN));
        citys.add(new CityDto(City.LIU_PAN_SHUI));
        citys.add(new CityDto(City.LIU_ZHOU));
        citys.add(new CityDto(City.LONG_NAN));
        citys.add(new CityDto(City.LONG_YAN));
        citys.add(new CityDto(City.LOU_DI));
        citys.add(new CityDto(City.LU_ZHOU));
        citys.add(new CityDto(City.LUO_HE));
        citys.add(new CityDto(City.LUO_YANG));
        citys.add(new CityDto(City.LV_LIANG));
        citys.add(new CityDto(City.MA_AN_SHAN));
        citys.add(new CityDto(City.MAO_MING));
        citys.add(new CityDto(City.MEI_SHAN));
        citys.add(new CityDto(City.MEI_ZHOU));
        citys.add(new CityDto(City.MIAN_YANG));
        citys.add(new CityDto(City.MU_DAN_JIANG));
        citys.add(new CityDto(City.NA_QU));
        citys.add(new CityDto(City.NAN_CHANG));
        citys.add(new CityDto(City.NAN_CHONG));
        citys.add(new CityDto(City.NAN_JING));
        citys.add(new CityDto(City.NAN_NING));
        citys.add(new CityDto(City.NAN_PING));
        citys.add(new CityDto(City.NAN_TONG));
        citys.add(new CityDto(City.NAN_YANG));
        citys.add(new CityDto(City.NEI_JIANG));
        citys.add(new CityDto(City.NING_BO));
        citys.add(new CityDto(City.NING_DE));
        citys.add(new CityDto(City.NU_JIANG));
        citys.add(new CityDto(City.PAN_JIN));
        citys.add(new CityDto(City.PAN_ZHI_HUA));
        citys.add(new CityDto(City.PING_DING_SHAN));
        citys.add(new CityDto(City.PING_LIANG));
        citys.add(new CityDto(City.PING_XIANG));
        citys.add(new CityDto(City.PU_ER));
        citys.add(new CityDto(City.PU_TIAN));
        citys.add(new CityDto(City.PU_YANG));
        citys.add(new CityDto(City.QI_QI_HA_ER));
        citys.add(new CityDto(City.QI_TAI_HE));
        citys.add(new CityDto(City.QIAN_DONG_NAN));
        citys.add(new CityDto(City.QIAN_NAN));
        citys.add(new CityDto(City.QIAN_XI_NAN));
        citys.add(new CityDto(City.QIN_HUANG_DAO));
        citys.add(new CityDto(City.QIN_ZHOU));
        citys.add(new CityDto(City.QING_DAO));
        citys.add(new CityDto(City.QING_YANG));
        citys.add(new CityDto(City.QING_YUAN));
        citys.add(new CityDto(City.QU_JING));
        citys.add(new CityDto(City.QU_ZHOU));
        citys.add(new CityDto(City.QUAN_ZHOU));
        citys.add(new CityDto(City.RI_KA_ZE));
        citys.add(new CityDto(City.RI_ZHAO));
        citys.add(new CityDto(City.SAN_MEN_XIA));
        citys.add(new CityDto(City.SAN_MING));
        citys.add(new CityDto(City.SAN_SHA));
        citys.add(new CityDto(City.SAN_YA));
        citys.add(new CityDto(City.SHAN_NAN));
        citys.add(new CityDto(City.SHAN_TOU));
        citys.add(new CityDto(City.SHAN_WEI));
        citys.add(new CityDto(City.SHANG_HAI));
        citys.add(new CityDto(City.SHANG_LUO));
        citys.add(new CityDto(City.SHANG_QIU));
        citys.add(new CityDto(City.SHANG_RAO));
        citys.add(new CityDto(City.SHAO_GUAN));
        citys.add(new CityDto(City.SHAO_XING));
        citys.add(new CityDto(City.SHAO_YANG));
        citys.add(new CityDto(City.SHEN_YANG));
        citys.add(new CityDto(City.SHEN_ZHEN));
        citys.add(new CityDto(City.SHI_JIA_ZHUANG));
        citys.add(new CityDto(City.SHI_YAN));
        citys.add(new CityDto(City.SHI_ZUI_SHAN));
        citys.add(new CityDto(City.SHUANG_YA_SHAN));
        citys.add(new CityDto(City.SHUO_ZHOU));
        citys.add(new CityDto(City.SI_PING));
        citys.add(new CityDto(City.SONG_YUAN));
        citys.add(new CityDto(City.SU_QIAN));
        citys.add(new CityDto(City.SU_ZHOU_1));
        citys.add(new CityDto(City.SU_ZHOU_4));
        citys.add(new CityDto(City.SUI_HUA));
        citys.add(new CityDto(City.SUI_NING));
        citys.add(new CityDto(City.SUI_ZHOU));
        citys.add(new CityDto(City.TA_CHENG));
        citys.add(new CityDto(City.TAI_AN));
        citys.add(new CityDto(City.TAI_WAN));
        citys.add(new CityDto(City.TAI_YUAN));
        citys.add(new CityDto(City.TAI_ZHOU_2));
        citys.add(new CityDto(City.TAI_ZHOU_4));
        citys.add(new CityDto(City.TANG_SHAN));
        citys.add(new CityDto(City.TIAN_JIN));
        citys.add(new CityDto(City.TIAN_SHUI));
        citys.add(new CityDto(City.TIE_LING));
        citys.add(new CityDto(City.TONG_CHUAN));
        citys.add(new CityDto(City.TONG_HUA));
        citys.add(new CityDto(City.TONG_LIAO));
        citys.add(new CityDto(City.TONG_LING));
        citys.add(new CityDto(City.TONG_REN));
        citys.add(new CityDto(City.TU_LU_FAN));
        citys.add(new CityDto(City.WEI_FANG));
        citys.add(new CityDto(City.WEI_HAI));
        citys.add(new CityDto(City.WEI_NAN));
        citys.add(new CityDto(City.WEN_SHAN));
        citys.add(new CityDto(City.WEN_ZHOU));
        citys.add(new CityDto(City.WU_HAI));
        citys.add(new CityDto(City.WU_HAN));
        citys.add(new CityDto(City.WU_HU));
        citys.add(new CityDto(City.WU_LAN_CHA_BU));
        citys.add(new CityDto(City.WU_LU_MU_QI));
        citys.add(new CityDto(City.WU_WEI));
        citys.add(new CityDto(City.WU_XI));
        citys.add(new CityDto(City.WU_ZHONG));
        citys.add(new CityDto(City.WU_ZHOU));
        citys.add(new CityDto(City.XI_AN));
        citys.add(new CityDto(City.XI_LIN_GUO_LE_MENG));
        citys.add(new CityDto(City.XI_NING));
        citys.add(new CityDto(City.XI_SHUANG_BAN_NA));
        citys.add(new CityDto(City.XIA_MEN));
        citys.add(new CityDto(City.XIAN_NING));
        citys.add(new CityDto(City.XIAN_YANG));
        citys.add(new CityDto(City.XIANG_GANG));
        citys.add(new CityDto(City.XIANG_TAN));
        citys.add(new CityDto(City.XIANG_XI));
        citys.add(new CityDto(City.XIANG_YANG));
        citys.add(new CityDto(City.XIAO_GAN));
        citys.add(new CityDto(City.XIN_XIANG));
        citys.add(new CityDto(City.XIN_YANG));
        citys.add(new CityDto(City.XIN_YU));
        citys.add(new CityDto(City.XIN_ZHOU));
        citys.add(new CityDto(City.XING_AN_MENG));
        citys.add(new CityDto(City.XING_TAI));
        citys.add(new CityDto(City.XU_CHANG));
        citys.add(new CityDto(City.XU_ZHOU));
        citys.add(new CityDto(City.XUAN_CHENG));
        citys.add(new CityDto(City.YA_AN));
        citys.add(new CityDto(City.YAN_AN));
        citys.add(new CityDto(City.YAN_BIAN));
        citys.add(new CityDto(City.YAN_CHENG));
        citys.add(new CityDto(City.YAN_TAI));
        citys.add(new CityDto(City.YANG_JIANG));
        citys.add(new CityDto(City.YANG_QUAN));
        citys.add(new CityDto(City.YANG_ZHOU));
        citys.add(new CityDto(City.YI_BIN));
        citys.add(new CityDto(City.YI_CHANG));
        citys.add(new CityDto(City.YI_CHUN_1));
        citys.add(new CityDto(City.YI_CHUN_2));
        citys.add(new CityDto(City.YI_LI_HA_SA_KE));
        citys.add(new CityDto(City.YI_YANG));
        citys.add(new CityDto(City.YIN_CHUAN));
        citys.add(new CityDto(City.YING_KOU));
        citys.add(new CityDto(City.YING_TAN));
        citys.add(new CityDto(City.YONG_ZHOU));
        citys.add(new CityDto(City.YU_LIN_2));
        citys.add(new CityDto(City.YU_LIN_4));
        citys.add(new CityDto(City.YU_SHU));
        citys.add(new CityDto(City.YU_XI));
        citys.add(new CityDto(City.YUE_YANG));
        citys.add(new CityDto(City.YUN_CHENG));
        citys.add(new CityDto(City.YUN_FU));
        citys.add(new CityDto(City.ZAO_ZHUANG));
        citys.add(new CityDto(City.ZHAN_JIANG));
        citys.add(new CityDto(City.ZHANG_JIA_JIE));
        citys.add(new CityDto(City.ZHANG_JIA_KOU));
        citys.add(new CityDto(City.ZHANG_YE));
        citys.add(new CityDto(City.ZHANG_ZHOU));
        citys.add(new CityDto(City.ZHAO_QING));
        citys.add(new CityDto(City.ZHAO_TONG));
        citys.add(new CityDto(City.ZHEN_JIANG));
        citys.add(new CityDto(City.ZHENG_ZHOU));
        citys.add(new CityDto(City.ZHONG_SHAN));
        citys.add(new CityDto(City.ZHONG_WEI));
        citys.add(new CityDto(City.ZHOU_KOU));
        citys.add(new CityDto(City.ZHOU_SHAN));
        citys.add(new CityDto(City.ZHU_HAI));
        citys.add(new CityDto(City.ZHU_MA_DIAN));
        citys.add(new CityDto(City.ZHU_ZHOU));
        citys.add(new CityDto(City.ZI_BO));
        citys.add(new CityDto(City.ZI_GONG));
        citys.add(new CityDto(City.ZI_YANG));
        citys.add(new CityDto(City.ZUN_YI));
        return citys;
    }

}
