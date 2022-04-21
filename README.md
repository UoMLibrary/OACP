# OACP

# oacpv2

Setup to run oacpv2 application in local laptop
================================================

**1.Eclipse IDE** for Enterprise Java and Web Developers - 2021-12

**2. Install JAVA 8**

**3. git clone repository from gitHub**
```
git clone https://github.com/UoMLibrary/OACP.git
```

**4. Import project to Eclipse.** <br/>
Eclipse-->"File"-->"Existing Maven Projects"

**5. PostgreSQL.**
```
CREATE USER **** WITH PASSWORD '******';
CREATE DATABASE oacp;
```

**6. Run the application**<br />
 open class: Oacpv2Application.java:  comment line 81-119.  Uncomment line 66-75. <br />
 Eclipse run application. ( tables will be automatically generated in Database.)


**7. visit application with brower**
 <br/> http://localhost:8080  <br/> 
   try test with these pureId: 99829207, 99569551, 99568563, 99568343, 99567735	



Deploy OACPV2
================================================
**1. Oacpv2Application.java** <br/>
    Uncomment line 81-119. and comment line 66-75.<br/>
    
**2. delete all local log information** <br/>
	"/src/main/resources/loginfo.log"<br/>
  
**3. Export application into WAR file.** <br/>

**4. copy WAR file into Virtual machines.** <br/>
```
scp oacp.war username@openaccessv2-test.library.manchester.ac.uk://home/username
```

**5.Run on Virtual machines**  <br/>
log in Test VM with password:
```
ssh username@openaccessv2-test.library.manchester.ac.uk
```

log in PRD VM with password:
```
ssh username@openaccessv2.library.manchester.ac.uk
```

after log into virtual machine, copy WAR file to /srv/webapps/ directory
```
sudo cp oacpv2.war /srv/webapp/ROOT.war
```

