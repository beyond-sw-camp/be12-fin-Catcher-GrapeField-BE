<p align="middle" style="margin: 0; padding: 0;">
  <img width="60%" src="https://github.com/user-attachments/assets/728096de-24af-4243-8910-eade5af71688">
</p>

<p align="middle">
<b>[한화시스템 BEYOND SW캠프 12기] Final Project</b>
<br>🧑🏻‍🌾 CATCHER 파수꾼 팀 🍇


## 😃 팀원 소개
<figure>
    <table>
      <tr>
        <td align="center"><img src="https://github.com/user-attachments/assets/fc09670f-0100-4deb-a070-a975200e5b44" width="180px"/></td>
        <td align="center"><img src="https://github.com/user-attachments/assets/e1378ccf-4afa-48cd-877d-e64b2ac69c19" width="180px"/></td>
        <td align="center"><img src="https://github.com/user-attachments/assets/e2dc5942-a318-4082-98e9-79a3919b0a07" width="180px"/></td>
	    <td align="center"><img src="https://github.com/user-attachments/assets/05128582-7e08-4fb5-a911-cf095af55af3" width="180px"/></td>
      </tr>
      <tr>
        <td align="center">팀장: <a href="https://github.com/bdt6246">김혜정</a></td>
        <td align="center">팀원: <a href="https://github.com/daydeiday">곽효림</a></td>
        <td align="center">팀원: <a href="https://github.com/J0a0J">김지원</a></td>
        <td align="center">팀원: <a href="https://github.com/s00ya">정지수</a></a></td>
      </tr>
    </table>
</figure>

---

## 목차
- [🛠 기술 스택](#-기술-스택)
- [🎨 GrapeField 서비스 소개](#-GrapeField-서비스-소개)
- [🏗️ 시스템 아키텍처](#-시스템-아키텍처)
- [📚 백엔드 프로젝트 목표](#-백엔드-프로젝트-목표)
- [📈 프로젝트 설계](#-프로젝트-설계)
- [🚀 핵심 로직 상세 설명](#-핵심-로직-상세-설명)
- [📂 프로젝트 폴더 바로가기](#-프로젝트-폴더-바로가기)
<br><br>

## 🛠 기술 스택

#### &nbsp;　[ Backend ]
&nbsp;&nbsp;&nbsp;&nbsp; ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/SMTP-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" style="border-radius: 5px;">


#### &nbsp;　[ DB ]
&nbsp;&nbsp;&nbsp;</a>
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white" style="border-radius: 5px;"> 
<img src="https://img.shields.io/badge/AmazonS3-e05242?style=for-the-badge&logo=Amazon_S3&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/redis-D24939?style=for-the-badge&logo=redis&logoColor=white" style="border-radius: 5px;">
![Elasticsearch](https://img.shields.io/badge/elasticsearch-%230377CC.svg?style=for-the-badge&logo=elasticsearch&logoColor=white)

#### &nbsp;　[ CI/CD ]
&nbsp;&nbsp;&nbsp;&nbsp;![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white" style="border-radius: 5px;">

#### &nbsp;　[ Etc ]
&nbsp;&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">
<img src="https://img.shields.io/badge/logstash-f3bd19?style=for-the-badge&logo=logstash&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/kibana-c41bb1?style=for-the-badge&logo=kibana&logoColor=white" style="border-radius: 5px;">


#### &nbsp;　[ Communication ]
&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/discord-326CE5?style=for-the-badge&logo=discord&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white" style="border-radius: 5px;">
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
<br><br><br>


## 📝 Grapefield 서비스 소개
### 프로젝트 배경
<p align="middle" style="margin: 0; padding: 0;">
<img width="90%" src="https://github.com/user-attachments/assets/d93c8050-f470-429f-af57-3d8387fc6be6">
</p>
<br>

#### 통합적인 문화행사 정보 제공 서비스
코로나 팬데믹 이후 오프라인 공연 및 전시회에 대한 수요가 급증하면서 관련 행사들이 활발히 개최되고 있다. 특히 기업, 기관의 홍보 담당자들은 브랜드 홍보 및 고객 접점을 확대하기 위한 전략으로 공연이나 박람회와 같은 오프라인 행사를 적극적으로 유치하거나 후원하고 있다.<br>
<p align="middle" style="margin: 0; padding: 0;">
	<img width="60%"  src="https://github.com/user-attachments/assets/06ca5c9b-b170-4dda-8c65-f990135127bc">
</p>
그러나 기존 정보는 개별 사이트나 SNS를 통해 확인해야 하며, 후기를 확인하거나 공유하는 과정이 번거롭다.
이러한 불편함을 해소하기 위하여 오프라인 문화 콘텐츠에 대한 <b>정보 제공</b>뿐만 아니라, 이용자들이 직접 참여하여 <b>실시간으로 소통</b>하고 <b>후기</b>를 나눌 수 있으며, 이러한 정보들이 축적되는 정보 통합 플랫폼 <b>GrapeField</b>를 기획하였다.

### 세부 기능
`Information`
- \#문화_콘텐츠_통합_정보 \#통합_검색 \#지난_공연_후기_아카이브
- 뮤지컬, 연극, 콘서트, 전시회, 클래식 등 다양한 오프라인 문화 콘텐츠에 대한 예매 정보, 상연 일정, 출연진, 세부 줄거리 등 상세한 정보를 제공한다.

`Calendar`
- \#예매일정_통합_제공 \#예매일절_알림_기능
- 예매처별로 나누어진 공연 및 전시 일정을 플랫폼 내에서 통합하여 확인할 수 있으며, 개별 사이트를 방문하지 않고도 손쉽게 일정을 관리할 수 있다. 또한 개인 일정을 등록하여 함께 관리하는 기능도 지원하여 사이트에서 놓친 일정도 문제 없이 관리할 수 있다.

`Chat`
- \#문화콘텐츠별_단독_채팅방 \#실시간_소통_제공
- 공연/전시 각각 개별로 실시간 채팅 기능을 제공하여 그를 주제로 이용자 간 즉각적인 소통이 가능하다. 관람 전 궁금한 사항을 묻거나, 관람 후 소감을 나누는 등 실시간 커뮤니케이션을 통해 현장의 분위기를 함께 느낄 수 있다.

---

## 🎯 시스템 아키텍처
![1팀 시스템 아키텍처 drawio](https://github.com/user-attachments/assets/81885197-9182-4919-9d37-378862333afc)

---

## 백엔드 프로젝트 목표
데이터베이스 설계 및 구축 : 정규화 및 무결성을 고려한 효율적인 DB 구조 설계</br>
RESTful API 개발 : 일관성 있는 URL 설계와 HTTP 메서드를 활용한 API 구현</br>
보안 강화 : 인증·인가(JWT, OAuth 등), 데이터 암호화 및 보안 정책 적용</br>
서버 성능 최적화 : 캐싱(Redis 등), 비동기 처리, 트래픽 분산 등 확장성 있는 서버 구조 설계</br>
로그 및 모니터링 시스템 구축, 부하분산 확인 : ELK(Stack), Loucst, Jmeter 등을 활용한 시스템 진단 및 유지보수 용이성 확보

---

## 프로젝트 설계

### [1. 프로젝트 기획서](https://docs.google.com/document/d/1EU-eQ4YGXyAWV_YPDVykpVMUiA__RazJUJNwZiR4Dc4/edit?usp=sharing)
### [2. 요구사항 정의서](https://docs.google.com/spreadsheets/d/1lH3kFL2whL5Pu4R99AMO2kA6stfM88ZQs0zwe05mVwA/edit?usp=sharing)
### [3. WBS](https://docs.google.com/spreadsheets/d/18U7T2FRrp6uOwkVXoEHOKjuOz3vogf99nwD2UxC60iU/edit?usp=sharing)
### [4. API명세서](https://grapefield.kro.kr/api/swagger-ui/index.html)

<br>

## ⚙️ 주요 기능
### 1. 통합 검색 기능

---
### 2. 채팅
### 3. 일정 관리

### 📝회원가입
<p align="middle" style="margin: 0; padding: 0;">
  <img src="https://github.com/user-attachments/assets/01190991-bbf7-4133-b0c4-cbecf42f5265">
</p>
<p align="middle" style="margin: 0; padding: 0;">
  <img src="https://github.com/user-attachments/assets/c654e568-b9cc-41cf-bf2e-5588bb59b34a">
</p>


### 🔐로그인	
![Screenshot 2025-05-19 at 07 57 54](https://github.com/user-attachments/assets/4e897751-257c-463b-a78a-919ccb6498bf)


<details>
<summary>메인 페이지<summary>
- 현재 진행 중이거나 예정된 **공연/전시회 정보를 최신순, 인기순으로 표시**
- **카테고리별 필터링 기능** 제공 (공연, 전시, 박람회 등)
- **추천 행사 및 하이라이트 코너** 제공
</details>



<details>
<summary>📅일정 캘린더<summary>
- **공연/전시 일정을 캘린더 형식으로 시각화**
- 예매처 링크, **선예매 / 일반예매** 구분 표시
- **관심 행사 알림 설정 기능**
</details>

<details>
<summary>검색<summary>

</details>

<details>
<summary>공연/전시 상세 페이지<summary>
- 행사 기본 정보 (제목, 일시, 장소, 가격 등) 제공
- **예매 정보 및 링크** 표시
- **후기 게시판 연결** 기능
- 실시간 채팅방 입장 버튼

![텐-상세페이지-채팅방입장](https://github.com/user-attachments/assets/0635ad09-a4cc-40df-86ec-2656553e7a58)

</details>


<details>
<summary>🌟 게시판<summary>
- **행사별 후기 게시판 제공**
- **별점, 상세 리뷰, 이미지 첨부** 기능
- **댓글 및 추천 기능**
- **검색 및 필터링 기능**
</details>

<details>
<summary>한줄평<summary>

</details>

<details>
<summary>💬 실시간 채팅방<summary>
	
![Screenshot 2025-05-19 at 08 27 30](https://github.com/user-attachments/assets/69461828-0884-4a47-baf4-7689640d1603)


- **공연/전시 행사별 실시간 채팅방 제공**
  - 과거 채팅 내용 조회 가능

![뮤지컬-인기채팅리스트](https://github.com/user-attachments/assets/8a98cac6-1c7b-441a-9f2a-2de867c43966)


- **공연/전시 행사별 채팅방 별 실시간 좋아요(하트) 송수신 기능 및 메세지 송수신 기능**

![채팅 3명 동시 접속-메세지-하트](https://github.com/user-attachments/assets/293cb264-c332-4d56-b79c-19b18eee12b5)

- **실시간 하트 증가율에 따른 인기 채팅방 목록 조회 기능**

![Screenshot 2025-05-19 at 08 27 30](https://github.com/user-attachments/assets/69461828-0884-4a47-baf4-7689640d1603)

- **하이라이트 시간대 자동 탐지 기능**
- **하이라이트 채팅구간 저장 시 AI API로 키워드 요약 기능**

</details>


<details>
<summary>🙋 마이페이지<summary>
- **관심 행사 목록** 확인
- **작성한 후기 관리**
- **알림 설정 및 관리**
- **프로필 설정**
</details>


<details>
<summary><summary>

</details>

## 🚀 핵심 로직 상세 설명
### [📃 프로젝트 Wiki](https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-FE/wiki/백엔드-프로젝트) <br><br>


## 📂 프로젝트 폴더 바로가기
### [📃 Frontend](https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-FE.git)
### [📃 Devops(Frontend)]()
<br>//TODO: 수정해야함 <br>
### [📃 Backend](https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-BE.git)
### [📃 Devops(Backend)]()
<br>//TODO: 수정해야함<br>
<br>
