package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FlatButton extends JButton {
    private Color hoverBackgroundColor = new Color(41, 128, 185);  // 鼠标悬停时的颜色
    private Color pressedBackgroundColor = new Color(52, 152, 219); // 按下时的颜色
    private Color normalBackgroundColor = new Color(52, 73, 94);    // 正常状态的颜色
    private Color textColor = Color.WHITE;                          // 文字颜色
    
    public FlatButton(String text) {
        super(text);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(normalBackgroundColor);
        setForeground(textColor);
        setFont(new Font("Arial", Font.BOLD, 16));
        
        // 添加鼠标事件监听
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverBackgroundColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(normalBackgroundColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(pressedBackgroundColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    if (getModel().isRollover()) {
                        setBackground(hoverBackgroundColor);
                    } else {
                        setBackground(normalBackgroundColor);
                    }
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制圆角矩形背景
        if (getModel().isEnabled()) {
            g2.setColor(getBackground());
        } else {
            g2.setColor(new Color(149, 165, 166)); // 禁用状态的颜色
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        // 绘制文字
        FontMetrics metrics = g2.getFontMetrics(getFont());
        Rectangle stringBounds = metrics.getStringBounds(getText(), g2).getBounds();
        
        int x = (getWidth() - stringBounds.width) / 2;
        int y = (getHeight() - stringBounds.height) / 2 + metrics.getAscent();
        
        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += 40;  // 增加按钮的宽度
        size.height += 10; // 增加按钮的高度
        return size;
    }
} 