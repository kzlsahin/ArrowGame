import processing.sound.*;

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
void settings(){
  dWidth=displayWidth-10;
  size(dWidth,dWidth/2);
}
void setup(){

  frameRate(30);
  //fullScreen();
  //surface.setResizable(true);
  background(0);
  
  yayY = height/2;
  yayX = 0.;
yayOk = new PVector((mouseX+yayX-yayX), (mouseY-yayY-yayY));
yayOk.normalize();
yayOkLength = width/25.;
yayOkCap = width / 200.;
vurdun = false;
topHavada = false;
hedefX = width*0.9;
hedefY = height*0.8;
hedefW = width*0.05;
hedefH = height*0.05;
hedefSpeed = scale*width/120.;
hedefSpeedX = random(4., hedefSpeed);
hedefSpeedY = random(4., hedefSpeed);
windResist = 0.05;
windSpeed = 0;
windSpeedChange = 1;
windTiming = 0;
frameSay = 0;
puan = 0;
shots = 0;
scale = 1;
topSpeed = scale*width/80.;
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

circleX = width*0.2;
circleY = height/2;
circleR = height*0.1;

  table = loadTable("scoreTable.csv", "header");
  
  /*table.addColumn("id");
  table.addColumn("kilss");
  table.addColumn("shots");*/
  profile = "mustafa";

  shotDownSound = new SqrOsc(this);
  
  //envShot data
  envShot = new Env(this);
  envFlying = new Env(this);
  envAtack = 0.08;
  encSustainTime = .2;
  envSustainLevel = 0.4;
  envReleaseTime = 0.4;
  

  
}


void draw(){
    background(0);
  
  if(!basladi){
    fill(255);
    textAlign(CENTER);
    textSize(height*0.05);
    textLeading(10);
    text("profil adı giriniz", width/2, height/2);
    text(profile, width/2, height/2+60);
    textSize(height*0.04);
    text("tamam için 'Enter'\nsilmek için 'backspace'\nok atmak için 'space' veya 'mouse tuslari'\nPause aç/kapa için 'p'", width/2, height/2+110);
    if(focused){
      text("active", width*0.1, height*0.1);
    } else {
      text("not active", width*0.1, height*0.1);
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
  if(sqrt(sq(hedefX+hedefW/2-circleX)+sq(hedefY+hedefH/2-circleY)) < circleR*0.9){
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
  textSize(height*0.05);
  text("puan = "+puan, width*0.8, height*0.05);
    
  noFill();
  rect(width*0.1,height*0.1, width*0.3, 0.05*height);
  fill(0, 255, 0, 75);
  rect(width*0.1,height*0.1, (atesArasiSay/mermiArasiSure)*(width*0.3), 0.05*height);

  if(gameLost){
    fill(255,0,0);
    textAlign(CENTER);
    text("Game Over Brother ", width*0.5, height*0.3);
    }
  }

}
void keyPressed(){
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
void mousePressed(){
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
    gravity = scale*width/10000.;
    speedY = direction.y*hiz;
    ates = true;
    isabet = false;
    numTop = i;
  }
    
    void displayTop(){
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
    void updatePosTop(){
      if(ates){
      x += speedX;
      speedX += 0.002*(windSpeed-speedX);
      speedY += gravity;
      y += speedY;
      }
      if(x < 0 || x > width || y > height-diameter*3.){
       ates = false;
       flyingArrow[numTop].stop();
      }
    }
    void vurdu(float hedefx, float hedefy, float hedefw, float hedefh){
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
       shotDownSound.play(320, 0.8);
       envShot.play(shotDownSound, envAtack, encSustainTime, envSustainLevel, envReleaseTime);
     }
    }
    }
    void canavarReset(){
      b = int(random(1,2.9));
      hedefX = random(0.5*width, 0.9*width);
      hedefY = random(0.1*height, 0.9*height);
      hedefSpeedX = random(4., hedefSpeed);
      hedefSpeedY = random(4., hedefSpeed);
    }
    
    void canvarReturn(int num){
      vicik = loadImage("vicik"+num+".png");
      canavar = loadImage("canavar"+num+".png");
    }
    
void shootArrow(){
  if(basladi && !gamePaused && focused){

    if(!gameLost && atesArasiSay >= mermiArasiSure){
    myTop[mermiSay] = new Top(yayX, yayY, topSpeed, yayOkCap, yayOk, mermiSay);
    atesArasiSay = 0;
    shots += 1;
    flyingArrow[mermiSay].play(0.8);
    envFlying.play(flyingArrow[mermiSay], 0.001, .1, 0.6, 3.);
  
      if(mermiSay < mermiSiniri-1){
        mermiSay += 1;
      }
      else{
        mermiSay = 0;
      }
 }
}
}

void showScore(){
  textSize(height*0.05);
  text("rekor : "+sonSkor+" ile "+rekortmenProfile+"'nın", width/2, height/2);
  text("Rekortmenin isabet/atis: "+table.getInt(rekortmenId, "kills")+" / "+table.getInt(rekortmenId, "shot"), width/2, height*0.6);
  text("senin skor: "+puan, width/2, height*0.7);
  text("Senin isabet/atis sayın: "+puan+" / "+shots, width/2, height*0.8);
}

//ruzgar ekliyoruz
void updateWind(){
  if(basladi && !gameLost && windVar){
  windSpeed += windSpeedChange*(1/(1+float(windTiming)));
  if(windTiming == 400){
    windTiming = 0;
    windSpeedChange += (randomGaussian()*4);
    windSpeedChange *= -1;
  }
    
  windTiming += 1;
  
  //Ok gösterge
  fill(255, 80);
  strokeWeight(2);
  rect(width*0.5, height*0.1, windSpeed*2, 5);
  line(width*0.5, height*0.1-5, width*0.5, height*0.1+10);
  textSize(height*0.04);
  text("wind Speed", width*0.5, height*0.05);
  //text(windSpeed, width*0.5, height*0.09);
} 
}
