/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrallyroadlinker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author jimakoskx
 */
public class JRallyRoadLinker extends JFrame implements ActionListener{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new JRallyRoadLinker();
    }
    
    
    Point2D.Double p1=new Point2D.Double(300,200);
    Point2D.Double p2=new Point2D.Double(500,250);
    Point2D.Double p3=new Point2D.Double(400,100);
    double platos=100;
    
    
    JPanel np=new JPanel(new GridLayout(1,4));
    JTextField jtfp1=new JTextField(p1.x+","+p1.y);
    JTextField jtfp2=new JTextField(p2.x+","+p2.y);
    JTextField jtfp3=new JTextField(p3.x+","+p3.y);
    JTextField jtfPlatos=new JTextField(""+platos);
    
    JPanel dr=new JPanel(){
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            paintingDrawPanel((Graphics2D)g);
        }
    };
    
    public JRallyRoadLinker(){
        super("useless atan2 ? Preparations for Random Rally Maps");
        Toolkit tk=Toolkit.getDefaultToolkit();
        setBounds(new Rectangle(tk.getScreenSize()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().add(dr,BorderLayout.CENTER);
        
        
        np.add(jtfp1);jtfp1.setToolTipText("Right Click+Shift Start point of first part of the road");
        np.add(jtfp2);jtfp2.setToolTipText("Right Click+Control End first part and START of second road");
        np.add(jtfp3);jtfp3.setToolTipText("Right Click on canvas !!!! .... END point of SECOND part of the road");
        np.add(jtfPlatos);jtfPlatos.setToolTipText("The width (not length) of the Road");
        getContentPane().add(np,BorderLayout.NORTH);
        
        
        jtfPlatos.addActionListener(this);
        jtfp1.addActionListener(this);
        jtfp2.addActionListener(this);
        jtfp3.addActionListener(this);
        dr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3){
                    JTextField jtf=jtfp3;
                    if(e.isShiftDown()){
                        jtf=jtfp1;
                    }
                    else if(e.isControlDown()){
                        jtf=jtfp2;
                    }
                    jtf.setText(e.getX()+","+e.getY());
                    actionPerformed(null);
                }
            }
        });
        dr.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
                jtfp3.setText(e.getX()+","+e.getY());
                actionPerformed(null);

            }
        });
        
        
        resetDrawings();
        
        setVisible(true);
        JOptionPane.showMessageDialog(this,"Drag you mouse\n\nor right click it alone /Shift/Control\n\nor enter TextFields to parse inputs"
                + "\n\nTrying to find the quicker and easier for processor \n\n"
                + "algo to define the max angle of the next road ...when generating random maps...\n\n"
                + "I ve so messed up with Math.atan2 ...and finally i totally escape atan2 usage ...making it all 'manual'");
    }
    
    public void parse(String text,Point2D p)throws Exception{
        StringTokenizer st=new StringTokenizer(text,",() []{}");
        int i=0;
        Vector<Double> ds=new Vector<>();
        String tok;
        while(st.hasMoreTokens() && i<2){
            tok=st.nextToken();
            ds.add(Double.parseDouble(tok));
            ++i;
        }
        if(i>1){
            p.setLocation(ds.get(0), ds.get(1));
        }
        else{
            throw new IllegalArgumentException("insufficient number of doubles for Point");
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try{        //System.out.println("reparsing");
            parse(jtfp1.getText(),p1);//System.out.println(""+p1);
            parse(jtfp2.getText(),p2);//System.out.println(""+p2);
            parse(jtfp3.getText(),p3);//System.out.println(""+p3);
            platos=Double.parseDouble(jtfPlatos.getText());//System.out.println(""+platos);
            //System.out.println("parsed ok?");
            resetDrawings();
        }catch(Exception exc){
            JOptionPane.showMessageDialog(this,exc+"");
        }
        
        
    }
    static double dg(double d){
        return Math.toDegrees(d);
    }
    public static final double rd360=2*Math.PI;
    public static final double rd90=Math.PI/2;
    
    
    
    boolean error;//purpose of program is to not allow too much angle for line23 relative to line12
    Path2D.Double outerPath=new Path2D.Double();//also secondary purpose to fill the outer corner with something
    Path2D.Double innerPath=new Path2D.Double();//>>>>
    double plhalf;
    double d21y,d21x,kl21;
    double d32y,d32x,kl32;
    double g12,g23;
    Path2D.Double path12=new Path2D.Double();
    Point2D.Double p120=new Point2D.Double();
    Point2D.Double p121=new Point2D.Double();
    Point2D.Double p122=new Point2D.Double();
    Point2D.Double p123=new Point2D.Double();
    
    
    Path2D.Double path23=new Path2D.Double();
    Point2D.Double p230=new Point2D.Double();
    Point2D.Double p231=new Point2D.Double();
    Point2D.Double p232=new Point2D.Double();
    Point2D.Double p233=new Point2D.Double();
    
    
    double c21Turn,c32Turn;//Y=AX+C===y=ax+c , To find intersection of parallels from turning side (inner corner) 
    Point2D.Double interTurn=new Point2D.Double();
    double c21AntiTurn,c32AntiTurn;// , To find intersection of parallels from ANTI-turning side (outer corner)
    Point2D.Double interAntiTurn=new Point2D.Double();
    Path2D.Double path12Fixed=new Path2D.Double();
    Path2D.Double path23Fixed=new Path2D.Double();
    void resetDrawings(){
        error=false;
        path12.reset();
        path23.reset();
        path12Fixed.reset();
        path23Fixed.reset();
        outerPath.reset();
        innerPath.reset();
        plhalf=platos/2;
        
        

        d21y=p2.y-p1.y;
        d21x=p2.x-p1.x;
        kl21=d21y/d21x;//we will need it in part 2
        if(p2.x>p1.x){
            if(p2.y>p1.y){g12=Math.atan(kl21);}
            else if(p2.y<p1.y){g12=2*Math.PI+Math.atan(kl21);}
            else{g12=0;}
        }
        else if(p2.x<p1.x){
            if(p2.y>p1.y){g12=Math.PI+Math.atan(kl21);}
            else if(p2.y<p1.y){g12=Math.PI+Math.atan(kl21);}
            else{g12=-Math.PI;}
        }
        else{
            if(p2.y>p1.y){g12=Math.PI/2;}
            else if(p2.y<p1.y){g12=Math.PI+Math.PI/2;}
            else{g12=java.lang.Double.NaN;}
        }
        
        d32y=p3.y-p2.y;
        d32x=p3.x-p2.x;
        kl32=d32y/d32x;//we will need it in part 2
        if(p3.x>p2.x){
            if(p3.y>p2.y){g23=Math.atan(kl32);}
            else if(p3.y<p2.y){g23=2*Math.PI+Math.atan(kl32);}
            else{g23=0;}
        }
        else if(p3.x<p2.x){
            if(p3.y>p2.y){g23=Math.PI+Math.atan(kl32);}
            else if(p3.y<p2.y){g23=Math.PI+Math.atan(kl32);}
            else{g23=-Math.PI;}
        }
        else{
            if(p3.y>p2.y){g23=Math.PI/2;}
            else if(p3.y<p2.y){g23=Math.PI+Math.PI/2;}
            else{g23=java.lang.Double.NaN;}
        }
        System.out.println(dg(g12)+" -> "+dg(g23));
        
        
        
        double g12p90=g12+rd90;
        double g12m90=g12-rd90;
        p120.setLocation(p2.x+plhalf*Math.cos(g12p90),p2.y+plhalf*Math.sin(g12p90));
        p121.setLocation(p1.x+plhalf*Math.cos(g12p90),p1.y+plhalf*Math.sin(g12p90));
        p122.setLocation(p1.x+plhalf*Math.cos(g12m90),p1.y+plhalf*Math.sin(g12m90));
        p123.setLocation(p2.x+plhalf*Math.cos(g12m90),p2.y+plhalf*Math.sin(g12m90));
        appendPol(path12,p120, p121, p122, p123);
        
        
        double g23p90=g23+rd90;
        double g23m90=g23-rd90;
        p230.setLocation(p3.x+plhalf*Math.cos(g23p90),p3.y+plhalf*Math.sin(g23p90));
        p231.setLocation(p2.x+plhalf*Math.cos(g23p90),p2.y+plhalf*Math.sin(g23p90));
        p232.setLocation(p2.x+plhalf*Math.cos(g23m90),p2.y+plhalf*Math.sin(g23m90));
        p233.setLocation(p3.x+plhalf*Math.cos(g23m90),p3.y+plhalf*Math.sin(g23m90));
        appendPol(path23,p230, p231, p232, p233);
        
        error=path12.contains(p230)||path12.contains(p233)||path12.contains(p3);
        //error=false;//uncomment to see the difference
        /////////////////////////////////////////////////////
        /////////BASIC PART till here///////////////////////
        /////////////////////////////////////////////////////
        
        if(g23<g12){//Positives executing quicker?
            g23+=rd360;
        }
        double dif=g23-g12;
        c21Turn=p120.y-p120.x*kl21;
        c32Turn=p230.y-p230.x*kl32;
        //y=a1x+c1 ==== y =a2x+c2<=> a1x+c1=a2x+c2<=> x=(c2-c1)/(a1-a2)...kai y= ....a1x+c1
            //////////////////////////////////////////////
        c21AntiTurn=p123.y-p123.x*kl21;
        c32AntiTurn=p232.y-p232.x*kl32;
        if(g23<g12+Math.PI){//Positives executing quicker?
            System.out.println("Positive Turn "+dg(dif));
            interTurn.x=(c32Turn-c21Turn)/(kl21-kl32);
            interTurn.y=interTurn.x*kl21+c21Turn;
            interAntiTurn.x=(c32AntiTurn-c21AntiTurn)/(kl21-kl32);
            interAntiTurn.y=interAntiTurn.x*kl21+c21AntiTurn;
            appendPol(outerPath,p2,p123,interAntiTurn,p232);
            appendPol(innerPath,p2,p232,p233,p230,interTurn,p120);
            error |= interAntiTurn.distance(p123)>p123.distance(p122);
        }
        else{
            dif-=rd360;
            System.out.println("Negative turn "+dg(dif));
            interAntiTurn.x=(c32Turn-c21Turn)/(kl21-kl32);
            interAntiTurn.y=interAntiTurn.x*kl21+c21Turn;
            interTurn.x=(c32AntiTurn-c21AntiTurn)/(kl21-kl32);
            interTurn.y=interTurn.x*kl21+c21AntiTurn;
            appendPol(outerPath,p2,p231,interAntiTurn,p120);
            appendPol(innerPath,p2,p123,interTurn,p233,p230,p231);
            error |= interAntiTurn.distance(p120)>p120.distance(p121);
            
        }
        dr.repaint();
    }
    
    
    public static void appendPol(Path2D path,Point2D.Double p1,Point2D.Double p2,Point2D.Double p3,Point2D.Double p4){
        path.moveTo(p1.x,p1.y);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.lineTo(p4.x,p4.y);
        path.lineTo(p1.x,p1.y);//instead of closePath?  
    }
    
    public static void appendPol(Path2D path,Point2D.Double p1,Point2D.Double p2,Point2D.Double p3,Point2D.Double p4,Point2D.Double p5){
        path.moveTo(p1.x,p1.y);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.lineTo(p4.x,p4.y);
        path.lineTo(p5.x,p5.y);
        path.lineTo(p1.x,p1.y);//instead of closePath?  
    }
    public static void appendPol(Path2D path,Point2D.Double p1,Point2D.Double p2,Point2D.Double p3,Point2D.Double p4,Point2D.Double p5,Point2D.Double p6){
        path.moveTo(p1.x,p1.y);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.lineTo(p4.x,p4.y);
        path.lineTo(p5.x,p5.y);
        path.lineTo(p6.x,p6.y);
        path.lineTo(p1.x,p1.y);//instead of closePath?  
    }
    
    public void paintingDrawPanel(Graphics2D g){
        if(error){
            g.setColor(Color.white);
        } 
        else{
            g.setColor(Color.green);
        }
        g.fill(g.getClip());
        
        
        g.setColor(Color.yellow);
        g.fill(outerPath);
        g.setColor(Color.cyan);
        g.fill(innerPath);
        g.setColor(Color.magenta);
        g.drawString("inner",(int)interTurn.x,(int)interTurn.y);
        g.drawString("outer",(int)interAntiTurn.x,(int)interAntiTurn.y);
        g.drawLine((int)interTurn.x,(int)interTurn.y,(int)interAntiTurn.x,(int)interAntiTurn.y);
        
        g.setColor(Color.black);
        g.drawString("p120",(int)p120.x,(int)p120.y);
        g.drawString("p121",(int)p121.x,(int)p121.y);
        g.drawString("p122",(int)p122.x,(int)p122.y);
        g.drawString("p123",(int)p123.x,(int)p123.y);
        
        g.drawString("p230",(int)p230.x,(int)p230.y);
        g.drawString("p231",(int)p231.x,(int)p231.y);
        g.drawString("p232",(int)p232.x,(int)p232.y);
        g.drawString("p233",(int)p233.x,(int)p233.y);
        
        g.setColor(Color.red);
        g.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
        g.draw(path12);
        if(error){
            g.drawString("ERROR", 10, 10);
            g.drawString("ERROR", 100, 100);
            g.drawString("ERROR", 300, 300);
            g.drawString("ERROR", 400, 400);
        }
        
        g.setColor(Color.green);
        g.drawLine((int)p2.x,(int)p2.y,(int)p3.x,(int)p3.y);
        g.draw(path23);
        
    }
    

}
