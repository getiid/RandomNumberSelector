package src;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;

public class SoundManager {
    private static Clip buttonClickClip;
    private static Clip numberGenerateClip;
    
    static {
        try {
            // 获取项目根目录的绝对路径
            String userDir = System.getProperty("user.dir");
            System.out.println("项目根目录: " + userDir);
            
            // 构建音效文件的完整路径
            File soundsDir = new File(userDir, "sounds");
            File clickFile = new File(soundsDir, "click.wav");
            File generateFile = new File(soundsDir, "generate.wav");
            
            // 打印详细的文件信息
            System.out.println("音效目录是否存在: " + soundsDir.exists());
            System.out.println("音效目录路径: " + soundsDir.getAbsolutePath());
            System.out.println("点击音效文件是否存在: " + clickFile.exists());
            System.out.println("点击音效文件路径: " + clickFile.getAbsolutePath());
            System.out.println("生成音效文件是否存在: " + generateFile.exists());
            System.out.println("生成音效文件路径: " + generateFile.getAbsolutePath());
            
            if (!clickFile.exists() || !generateFile.exists()) {
                System.err.println("无法找到音效文件！");
                // 列出sounds目录中的所有文件
                if (soundsDir.exists() && soundsDir.isDirectory()) {
                    System.out.println("sounds目录中的文件：");
                    for (File file : soundsDir.listFiles()) {
                        System.out.println(" - " + file.getName());
                    }
                }
            } else {
                // 加载按钮点击音效
                AudioInputStream clickAis = AudioSystem.getAudioInputStream(clickFile);
                buttonClickClip = AudioSystem.getClip();
                buttonClickClip.open(clickAis);
                System.out.println("按钮音效加载成功！");
                
                // 加载数字生成音效
                AudioInputStream generateAis = AudioSystem.getAudioInputStream(generateFile);
                numberGenerateClip = AudioSystem.getClip();
                numberGenerateClip.open(generateAis);
                System.out.println("数字生成音效加载成功！");
            }
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("音频文件格式不支持: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("音频设备不可用: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("读取音频文件出错: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("加载音效时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void playButtonClick() {
        try {
            playSound(buttonClickClip);
        } catch (Exception e) {
            System.err.println("播放按钮音效时出错: " + e.getMessage());
        }
    }
    
    public static void playNumberGenerate() {
        try {
            playSound(numberGenerateClip);
        } catch (Exception e) {
            System.err.println("播放数字生成音效时出错: " + e.getMessage());
        }
    }
    
    private static void playSound(Clip clip) {
        try {
            if (clip != null && clip.isOpen()) {
                clip.setFramePosition(0);
                clip.start();
            } else {
                System.err.println("音效剪辑为空或未打开");
                if (clip == null) {
                    System.err.println("音效剪辑为null");
                } else if (!clip.isOpen()) {
                    System.err.println("音效剪辑未打开");
                }
            }
        } catch (Exception e) {
            System.err.println("播放音效时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 