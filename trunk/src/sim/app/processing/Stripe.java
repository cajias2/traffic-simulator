package sim.app.processing;
import processing.core.PApplet;


public class Stripe
{
    PApplet parent;
    
    float x;
    float speed;
    float w;
    boolean mouse;
    
    
    Stripe(PApplet parent_)
    {
        parent = parent_;
        x = 0;
        speed = parent.random(1);
        w = parent.random(10,30);
        mouse= false;        
    }    
    
    void display()
    {
        parent.fill(255, 100);
        parent.noStroke();
        parent.rect(x, 0, w, parent.height/2);        
    }
    
    void move()
    {
        x += speed;
        if(x > parent.width+20)
            x = -20;
    }
}
