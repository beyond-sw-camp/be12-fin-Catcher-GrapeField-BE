package com.example.grapefield.chat.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class EmojiReplaceService {
    private static final Map<String, String> emojiMap = new LinkedHashMap<>();
    static {
        emojiMap.put(":rolling_on_the_floor_laughing:", "🤣");
        emojiMap.put(":loudly_crying_face:", "😭");
        emojiMap.put(":face_blowing_a_kiss:", "😘");
        emojiMap.put(":smiling_face_with_hearts:", "🥰");
        emojiMap.put(":smiling_face_with_smiling_eyes:", "😊");
        emojiMap.put(":beaming_face_with_smiling_eyes:", "😁");
        emojiMap.put(":smiling_face_with_open_mouth_and_tightly_closed_eyes:", "😆");
        emojiMap.put(":smiling_face_with_halo:", "😇");
        emojiMap.put(":winking_face:", "😉");
        emojiMap.put(":upside_down_face:", "🙃");
        emojiMap.put(":thinking_face:", "🤔");
        emojiMap.put(":face_with_hand_over_mouth:", "🤭");
        emojiMap.put(":shushing_face:", "🤫");
        emojiMap.put(":face_with_symbols_on_mouth:", "🤬");
        emojiMap.put(":red_heart:", "❤️");
        emojiMap.put(":red-heart:", "❤️");
        emojiMap.put(":100:", "💯");
        emojiMap.put(":thumbs_up:", "👍");
        emojiMap.put(":thumbs-up:", "👍");
        emojiMap.put(":smiling_face:", "☺️");
        emojiMap.put(":face_with_steam_from_nose:", "😤");
        emojiMap.put(":hamster:", "🐹");
        emojiMap.put(":smiling_face_with_heart_eyes:", "😍");
        emojiMap.put(":face_with_heart_eyes:", "😍");
        emojiMap.put(":face-red-heart-shape:", "🤩");
        emojiMap.put(":kiss:", "💋");
        emojiMap.put(":slightly_smiling_face:", "🙂");
        emojiMap.put(":wavy_dash:", "〰️");
        emojiMap.put(":see_no_evil_monkey:", "🙈");
        emojiMap.put(":love_you_gesture:", "🤟");
        emojiMap.put(":virtualhug:", "🤗");
        emojiMap.put(":sparkles:", "✨");
        emojiMap.put(":grinning_face:", "😀");
        emojiMap.put(":grinning_face_with_smiling_eyes:", "😁");
        emojiMap.put(":face_with_tears_of_joy:", "😂");
        emojiMap.put(":smiling_face_with_open_mouth:", "😃");
        emojiMap.put(":smiling_face_with_open_mouth_and_smiling_eyes:", "😄");
        emojiMap.put(":smiling_face_with_open_mouth_and_cold_sweat:", "😅");
        emojiMap.put(":hugging_face:", "🤗");
        emojiMap.put(":folded_hands:", "🙏");
        emojiMap.put(":sparkling_heart:", "💖");
        emojiMap.put(":fire:", "🔥");
        emojiMap.put(":heart_on_fire:", "❤️‍🔥");
        emojiMap.put(":cat:", "🐱");
        emojiMap.put(":dog:", "🐶");
        emojiMap.put(":whale:", "🐳");
        emojiMap.put(":heart_exclamation:", "❣️");
        emojiMap.put(":exclamation_mark:", "❗");
        emojiMap.put(":check_mark:", "✔️");
        emojiMap.put(":white_heavy_check_mark:", "✅");
        emojiMap.put(":green_heart:", "💚");
        emojiMap.put(":blue_heart:", "💙");
        emojiMap.put(":yellow_heart:", "💛");
        emojiMap.put(":purple_heart:", "💜");
        emojiMap.put(":orange_heart:", "🧡");
        emojiMap.put(":black_heart:", "🖤");
        emojiMap.put(":pink_heart:", "🩷");
        emojiMap.put(":flushed_face:", "😳");
        emojiMap.put(":pleading_face:", "🥺");
        emojiMap.put(":angry_face:", "😠");
        emojiMap.put(":pouting_face:", "😡");
        emojiMap.put(":crying_face:", "😢");
        emojiMap.put(":persevering_face:", "😣");
        emojiMap.put(":disappointed_but_relieved_face:", "😥");
        emojiMap.put(":zipper_mouth_face:", "🤐");
        emojiMap.put(":money_mouth_face:", "🤑");
        emojiMap.put(":face_with_raised_eyebrow:", "🤨");
        emojiMap.put(":neutral_face:", "😐");
        emojiMap.put(":expressionless_face:", "😑");
        emojiMap.put(":unamused_face:", "😒");
        emojiMap.put(":face_with_rolling_eyes:", "🙄");
        emojiMap.put(":grimacing_face:", "😬");
        emojiMap.put(":lying_face:", "🤥");
        emojiMap.put(":relieved_face:", "😌");
        emojiMap.put(":pensive_face:", "😔");
        emojiMap.put(":sleepy_face:", "😪");
        emojiMap.put(":drooling_face:", "🤤");
        emojiMap.put(":sleeping_face:", "😴");
        emojiMap.put(":face_with_medical_mask:", "😷");
        emojiMap.put(":face_with_thermometer:", "🤒");
        emojiMap.put(":face_with_head_bandage:", "🤕");
        emojiMap.put(":nauseated_face:", "🤢");
        emojiMap.put(":face_vomiting:", "🤮");
        emojiMap.put(":sneezing_face:", "🤧");
        emojiMap.put(":hot_face:", "🥵");
        emojiMap.put(":cold_face:", "🥶");
        emojiMap.put(":woozy_face:", "🥴");
        emojiMap.put(":dizzy_face:", "😵");
        emojiMap.put(":exploding_head:", "🤯");
        emojiMap.put(":cowboy_hat_face:", "🤠");
        emojiMap.put(":partying_face:", "🥳");
        emojiMap.put(":disguised_face:", "🥸");
        emojiMap.put(":smiling_face_with_sunglasses:", "😎");
        emojiMap.put(":nerd_face:", "🤓");
        emojiMap.put(":face_with_monocle:", "🧐");
        emojiMap.put(":confused_face:", "😕");
        emojiMap.put(":worried_face:", "😟");
        emojiMap.put(":slightly_frowning_face:", "🙁");
        emojiMap.put(":frowning_face:", "☹️");
        emojiMap.put(":face_with_open_mouth:", "😮");
        emojiMap.put(":hushed_face:", "😯");
        emojiMap.put(":astonished_face:", "😲");
        // 필요시 추가 등록 가능
    }

    /**
     * 주어진 텍스트에서 emojiMap의 키값을 실제 이모지로 치환
     */
    public String replaceEmojis(String input) {
        String output = input;
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }
}
