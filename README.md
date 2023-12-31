# Bike Reviewer
#### Web application, designed for easy reviews writing by clients and content editing by admins.
### Features:
- Pagination and sorting
- Search
- Stars rating
- Authentication & authorization
- Admin panel: data and users management
- User profile with ability to change password and nickname
- Adaptive interface for users with different roles
### Technology stack:
- Java 17
- Spring Boot 3.1.4
- Spring Web&Security
- Apache Tomcat
- Thymeleaf (+Extras)
- PostgreSQL
- Spring Data
- Flyway
- Hibernate Validator
- Lombok
- Bootstrap

### Installation instructions:
1. Clone project.
2. Create PostgreSQL database and set URL, username and password in application.properties file.
3. Deploy and start up application in server (by default - using Apache Tomcat).
4. Log in using default users: 
   * | Username | Password |
     |----------|----------|
     | admin    | admin    |
     | stuff    | stuff    |
     | client   | client   |
   * After this, you have to create real users, and default users must be removed.
5. Roles explained:
   * ADMIN: has all authorities.
   * STUFF: unlike ADMIN, cannot manipulate User entities, nor delete any other entities except Reviews.
   * CLIENT: can browse through public pages, write Reviews and edit his own data.
   

