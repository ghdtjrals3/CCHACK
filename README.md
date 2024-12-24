# 🌱 Spring Boot Frameworks 3.x, MariaDB (수업 템플릿)

> **Java 17 기반 실습 코드**  
> Spring Boot 3.x는 Java 8 지원이 불가하므로, 본 실습은 Java 17 기반으로 작성되었습니다.  
> 공유되는 프로그래밍 코드는 한국폴리텍대학 서울강서캠퍼스 빅데이터과 수업에서 사용된 코드입니다.

---

### 📚 **작성자**
- **한국폴리텍대학 서울강서캠퍼스 빅데이터과**  
- **이협건 교수**  
- ✉️ [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  
- 🔗 [빅데이터학과 입학 상담 오픈채팅방](https://open.kakao.com/o/gEd0JIad)

---

## 🚀 주요 실습 내용

1. **Spring Boot Frameworks 이해를 위해 공지사항만 구현**

---

## 🛠️ 주요 적용 프레임워크

- **Spring Boot Frameworks**

---

## 📩 문의 및 입학 상담

- 📧 **이메일**: [hglee67@kopo.ac.kr](mailto:hglee67@kopo.ac.kr)  
- 💬 **입학 상담 오픈채팅방**: [바로가기](https://open.kakao.com/o/gEd0JIad)

---

## 💡 **우리 학과 소개**
- 한국폴리텍대학 서울강서캠퍼스 빅데이터과는 **클라우드 컴퓨팅, 인공지능, 빅데이터 기술**을 활용하여 소프트웨어 개발자를 양성하는 학과입니다.  
- 학과에 대한 더 자세한 정보는 [학과 홈페이지](https://www.kopo.ac.kr/kangseo/content.do?menu=1547)를 참고하세요.

---

## 📦 **설치 및 실행 방법**

### 1. 레포지토리 클론
- 아래 명령어를 실행하여 레포지토리를 클론합니다.

```bash
git clone https://github.com/Hyeopgeon-Lee/SpringBootBasicTemplate.git
cd SpringBootBasicTemplate
```

### 2. MariaDB 설정
application.yml 또는 application.properties 파일에서 데이터베이스 설정 정보를 업데이트합니다.

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/your_database
    username: your_username
    password: your_password
```

아래는 의존성 설치부터 시작하는 설치 및 실행 방법입니다.

### 3. 의존성 설치
아래 명령어를 실행하여 Maven 의존성을 설치합니다:
```bash
mvn clean install
```

### 4. 애플리케이션 실행
```bash
mvn spring-boot:run
```
