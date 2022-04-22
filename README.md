# OACP

Setup to run OACP application in local laptop
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


