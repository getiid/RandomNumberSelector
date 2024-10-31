package src;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private static final String BUTTON_CLICK_SOUND = "sounds/button_click.wav";
    private static final String NUMBER_GENERATE_SOUND = "sounds/number_generate.wav";
    
    static {
        // 确保音效文件夹存在
        File soundDir = new File("sounds");
        if (!soundDir.exists()) {
            soundDir.mkdir();
        }
        
        // 检查音效文件是否存在并输出状态
        checkSoundFile(BUTTON_CLICK_SOUND);
        checkSoundFile(NUMBER_GENERATE_SOUND);
    }
    
    private static void checkSoundFile(String path) {
        File soundFile = new File(path);
        if (soundFile.exists()) {
            System.out.println(path + " 音效加载成功！");
        } else {
            System.out.println("警告: " + path + " 音效文件不存在！");
        }
    }

    public static void playButtonClick() {
        try {
            playSound(BUTTON_CLICK_SOUND);
        } catch (IOException e) {
            System.out.println("播放按钮音效失败: " + e.getMessage());
        }
    }

    public static void playNumberGenerate() {
        try {
            playSound(NUMBER_GENERATE_SOUND);
        } catch (IOException e) {
            System.out.println("播放生成音效失败: " + e.getMessage());
        }
    }

    private static void playSound(String soundFile) throws IOException {
        try {
            File file = new File(soundFile);
            if (!file.exists()) {
                return; // 如果文件不存在，静默返回
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            
            // 添加监听器在播放完成后关闭资源
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    try {
                        clip.close();
                        audioIn.close();
                    } catch (IOException e) {
                        System.out.println("关闭音频资源失败: " + e.getMessage());
                    }
                }
            });
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println("播放音效时出错: " + e.getMessage());
        }
    }
} 