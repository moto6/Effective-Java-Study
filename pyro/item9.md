# Item 9: try-finally 보다는 try-with-resources 를 사용하라

close 메서드를 호출해 직접 닫아줘야 하는 경우에는 try-with-resources 를 사용하는게 실수를 줄일 수 있다.

권장되는 자원: InputStream, OutputStream, java.sql.Connection

try-with-resources 를 권장하는 이유는 try-finally 보다 코드가 예뻐서 이기도 하지만, 그보다 근본적인 장점은 개발자들이 close 를 깜빡하는 실수를 방지해준다는 점이다.

C 프로그래밍에서 free 는 초보개발자가 대충하다가 쉽게 실수하는 부분이라는 것을 상기해보라.

## try-finally

```java
        String sql = "INSERT INTO `user` (`email`, `name`) values (?, ?) ";
        Connection conn = persistentDataSource.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SqlException("유저 저장에 실패했습니다.");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new SqlException("Connection을 끊는데 실패했습니다. timeout 에러가 뜰 확률이 생깁니다.");
            }
        }
```

## try-with-resources

```java
        String sql = "INSERT INTO `user` (`email`, `name`) values (?, ?) ";
        try (Connection conn = persistentDataSource.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SqlException("유저 저장에 실패했습니다.");
        }
```
