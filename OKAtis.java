import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class OKAtis extends PApplet {



String x;
PVector yayOk;
float yayX, yayY, yayOkLength, yayOkCap;
boolean vurdun, topHavada, gameLost, kaydet, basladi, gamePaused;
int frameSay,  mermiSay, mermiArasiSure, atesArasiSay, b;
int mermiSiniri = 15;
Top[] myTop = new Top[mermiSiniri];
float hedefX, hedefY, hedefW, hedefH, hedefSpeed, hedefSpeedX, hedefSpeedY, vicikX, vicikY;
PImage canavar, vicik, ok;
float circleX, circleY, circleR;
int puan, shots, sonSkor, rekortmenId;
Table table;
String profile, rekortmenProfile;
float scale, topSpeed;
SqrOsc shotDownSound;
WhiteNoise[] flyingArrow = new WhiteNoise[mermiSiniri];;
Env envShot, envFlying;
float envAtack, encSustainTime, envSustainLevel, envReleaseTime;
float windResist, windSpeedChange, windSpeed;
int windTiming;
Boolean windVar;
int dWidth;
//SoundFile[] soundVicik = new SoundFile[2];
public void settings(){
  dWidth=displayWidth-10;
  size(dWidth,dWidth/2);
}
public void setup(){

  frameRate(30);
  //fullScreen();
  //surface.setResizable(true);
  background(0);
  
  yayY = height/2;
  yayX = 0.f;
yayOk = new PVector((mouseX+yayX-yayX), (mouseY-yayY-yayY));
yayOk.normalize();
yayOkLength = width/25.f;
yayOkCap = width / 200.f;
vurdun = false;
topHavada = false;
hedefX = width*0.9f;
hedefY = height*0.8f;
hedefW = width*0.05f;
hedefH = height*0.05f;
hedefSpeed = scale*width/120.f;
hedefSpeedX = random(4.f, hedefSpeed);
hedefSpeedY = random(4.f, hedefSpeed);
windResist = 0.05f;
windSpeed = 0;
windSpeedChange = 1;
windTiming = 0;
frameSay = 0;
puan = 0;
shots = 0;
scale = 1;
topSpeed = scale*width/80.f;
mermiArasiSure = 15;
atesArasiSay = mermiArasiSure;
windVar = true;

    for(int i = 0; i < mermiSiniri; i++){
    myTop[i] = new Top();
    flyingArrow[i] = new WhiteNoise(this);
    }
    
b = 2;
canavar = loadImage("canavar"+b+".png");
vicik = loadImage("vicik"+b+".png");
//soundVicik[0] = new SoundFile(this, "voice2.mp3");
//soundVicik[1] = new SoundFile(this, "voice1.mp3");
ok = loadImage("ok.png");

gameLost = false;
kaydet = false;
basladi = false;
gamePaused = false;

circleX = width*0.2f;
circleY = height/2;
circleR = height*0.1f;

  table = loadTable("scoreTable.csv", "header");
  
  /*table.addColumn("id");
  table.addColumn("kilss");
  table.addColumn("shots");*/
  profile = "mustafa";

  shotDownSound = new SqrOsc(this);
  
  //envShot data
  envShot = new Env(this);
  envFlying = new Env(this);
  envAtack = 0.08f;
  encSustainTime = .2f;
  envSustainLevel = 0.4f;
  envReleaseTime = 0.4f;
  

  
}


public void draw(){
    background(0);
  
  if(!basladi){
    fill(255);
    textAlign(CENTER);
    textSize(height*0.05f);
    textLeading(10);
    text("profil adı giriniz", width/2, height/2);
    text(profile, width/2, height/2+60);
    textSize(height*0.04f);
    text("tamam için 'Enter'\nsilmek için 'backspace'\nok atmak için 'space' veya 'mouse tuslari'\nPause aç/kapa için 'p'", width/2, height/2+110);
    if(focused){
      text("active", width*0.1f, height*0.1f);
    } else {
      text("not active", width*0.1f, height*0.1f);
    }
  }
  
  else if(gamePaused || !focused){
    textAlign(CENTER);
    fill(255);
    text("game paused, press 'p' ", width/2, height/2);
  }
  else if(!gamePaused && focused){
  yayOk.x = mouseX-yayX;
  yayOk.y = mouseY-yayY;
  yayOk.normalize();
  
  fill(255);
  strokeWeight(4);
  stroke(255);
  line(yayX,yayY, yayX+yayOkLength*yayOk.x, yayY+yayOkLength*yayOk.y);
  
  ellipse(circleX, circleY, circleR, circleR);
  
  if(!vurdun && !gameLost){
    hedefX += hedefSpeedX;
    hedefY += hedefSpeedY;
    
    if(hedefX < 0 || hedefX > width-hedefW){
      hedefSpeedX *= -1;
    }
    if(hedefY > height-hedefH || hedefY < 0){
      hedefSpeedY *= -1;
    }
  //rectMode(CORNER);
  //rect(hedefX, hedefY, hedefW, hedefH);
  image(canavar, hedefX, hedefY, hedefW+1, hedefH+1);
  }
  
   //Oyunu kaybetme
  if(sqrt(sq(hedefX+hedefW/2-circleX)+sq(hedefY+hedefH/2-circleY)) < circleR*0.9f){
    gameLost = true;
  }
  
  if(gameLost && !kaydet){
    TableRow newRow = table.addRow();
    newRow.setInt("id", table.getRowCount() - 1);
    newRow.setInt("kills", puan);
    newRow.setInt("shot", shots);
    newRow.setString("profile", profile);
    saveTable(table, "data/scoreTable.csv"); 
    
    int val = 0;
    for(TableRow row : table.rows()){
      if(row.getInt("kills") > val){
        val = row.getInt("kills");
        rekortmenId = row.getInt("id");
        rekortmenProfile = row.getString("profile");
      } else if(row.getInt("kills") == val && row.getInt("shot") > shots){
        rekortmenId = row.getInt("id");
        rekortmenProfile = row.getString("profile");
      }
    }
    sonSkor = val;
    kaydet = true;
  }
    
  if(gameLost){
    showScore();
  }
 
  if(!gameLost){
  for (int i = 0;i < mermiSiniri; i++){
  myTop[i].updatePosTop();
  myTop[i].displayTop();
  myTop[i].vurdu(hedefX, hedefY, hedefW, hedefH);
  }
     updateWind();   
  }
  
  if(vurdun&& frameCount < frameSay+30){
    text("vurdun",width/2,height/2);
    image(vicik,vicikX,vicikY, hedefW, hedefH);
    }
  else if(vurdun){
   vurdun = false; 
   vicik = loadImage("vicik"+b+".png");
   canavar = loadImage("canavar"+b+".png");
    }
    
  if(atesArasiSay<mermiArasiSure){
  atesArasiSay += 1;
  }
  textSize(height*0.05f);
  text("puan = "+puan, width*0.8f, height*0.05f);
    
  noFill();
  rect(width*0.1f,height*0.1f, width*0.3f, 0.05f*height);
  fill(0, 255, 0, 75);
  rect(width*0.1f,height*0.1f, (atesArasiSay/mermiArasiSure)*(width*0.3f), 0.05f*height);

  if(gameLost){
    fill(255,0,0);
    textAlign(CENTER);
    text("Game Over Brother ", width*0.5f, height*0.3f);
    }
  }

}
public void keyPressed(){
  if(!basladi && keyPressed){
        if(key == ENTER){
          basladi = true; 
        } 
       else if(key == BACKSPACE){
          profile = "";
        }    
       else { 
          profile += key;
        }
  }
  if(keyPressed && basladi && (key == 'p' || key == 'P')){
   if(!gamePaused ){
    gamePaused = true; 
    }
   else if(gamePaused){
    gamePaused = false;
    }
  }
  if(keyPressed && basladi && key == ' '){
    shootArrow();
  }
}
public void mousePressed(){
  shootArrow();
 
 if(gameLost){
   setup();
 }
}
class Top {
  
  float speedX, speedY, gravity;
  float diameter;
  float x, y;
  PVector direction;
  boolean ates, isabet;
  int numTop;
  
  Top(){
    ates = false;
    isabet = true;
  }
  Top (float mX, float mY, float hiz, float cap, PVector aim, int i){
    x = mX;
    y = mY;
    diameter = cap;
    direction = aim;
    speedX = direction.x*hiz;
    gravity = scale*width/10000.f;
    speedY = direction.y*hiz;
    ates = true;
    isabet = false;
    numTop = i;
  }
    
    public void displayTop(){
      if(!isabet){
      fill(255);
     //ellipse(x, y, diameter, diameter); 
     pushMatrix();
     translate(x, y);
     if(speedX>0){
     rotate((atan(speedY/speedX)));}
     else{rotate(PI+(atan(speedY/speedX)));
     }
     image(ok, 0, 0, diameter*6, diameter);
     popMatrix();
    }
   }
    public void updatePosTop(){
      if(ates){
      x += speedX;
      speedX += 0.002f*(windSpeed-speedX);
      speedY += gravity;
      y += speedY;
      }
      if(x < 0 || x > width || y > height-diameter*3.f){
       ates = false;
       flyingArrow[numTop].stop();
      }
    }
    public void vurdu(float hedefx, float hedefy, float hedefw, float hedefh){
     if(ates && x<hedefx+hedefw && x>hedefx && y<hedefy+hedefh && y>hedefy){
       //soundVicik[b-1].play();
       vurdun = true;
       isabet = true;
       ates = false;
       vicikX = hedefX;
       vicikY = hedefY;
       frameSay = frameCount;
       puan += 1;
       canavarReset();
       flyingArrow[numTop].stop();
       shotDownSound.play(320, 0.8f);
       envShot.play(shotDownSound, envAtack, encSustainTime, envSustainLevel, envReleaseTime);
     }
    }
    }
    public void canavarReset(){
      b = PApplet.parseInt(random(1,2.9f));
      hedefX = random(0.5f*width, 0.9f*width);
      hedefY = random(0.1f*height, 0.9f*height);
      hedefSpeedX = random(4.f, hedefSpeed);
      hedefSpeedY = random(4.f, hedefSpeed);
    }
    
    public void canvarReturn(int num){
      vicik = loadImage("vicik"+num+".png");
      canavar = loadImage("canavar"+num+".png");
    }
    
public void shootArrow(){
  if(basladi && !gamePaused && focused){

    if(!gameLost && atesArasiSay >= mermiArasiSure){
    myTop[mermiSay] = new Top(yayX, yayY, topSpeed, yayOkCap, yayOk, mermiSay);
    atesArasiSay = 0;
    shots += 1;
    flyingArrow[mermiSay].play(0.8f);
    envFlying.play(flyingArrow[mermiSay], 0.001f, .1f, 0.6f, 3.f);
  
      if(mermiSay < mermiSiniri-1){
        mermiSay += 1;
      }
      else{
        mermiSay = 0;
      }
 }
}
}

public void showScore(){
  textSize(height*0.05f);
  text("rekor : "+sonSkor+" ile "+rekortmenProfile+"'nın", width/2, height/2);
  text("Rekortmenin isabet/atis: "+table.getInt(rekortmenId, "kills")+" / "+table.getInt(rekortmenId, "shot"), width/2, height*0.6f);
  text("senin skor: "+puan, width/2, height*0.7f);
  text("Senin isabet/atis sayın: "+puan+" / "+shots, width/2, height*0.8f);
}

//ruzgar ekliyoruz
public void updateWind(){
  if(basladi && !gameLost && windVar){
  windSpeed += windSpeedChange*(1/(1+PApplet.parseFloat(windTiming)));
  if(windTiming == 400){
    windTiming = 0;
    windSpeedChange += (randomGaussian()*4);
    windSpeedChange *= -1;
  }
    
  windTiming += 1;
  
  //Ok gösterge
  fill(255, 80);
  strokeWeight(2);
  rect(width*0.5f, height*0.1f, windSpeed*2, 5);
  line(width*0.5f, height*0.1f-5, width*0.5f, height*0.1f+10);
  textSize(height*0.04f);
  text("wind Speed", width*0.5f, height*0.05f);
  //text(windSpeed, width*0.5, height*0.09);
} 
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "OKAtis" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
