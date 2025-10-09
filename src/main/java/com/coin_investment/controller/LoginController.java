package com.coin_investment.controller;

// JavaFX 활용을 위한 요소들
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

//
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//++ 회원가입 하이퍼링크 추가
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;
import java.io.IOException;


public class LoginController {
    @FXML
    private TextField idField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void loginButtonAction(ActionEvent event){
        String username = idField.getText();
        String password = passwordField.getText();

        // HttpClient 클래스 객체 생성
        HttpClient client = HttpClient.newHttpClient();

        // 서버로 보낼 JSON 데이터 생성
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        // String.format() : 문자열의 형식을 미리 정해놓고, 원하는 값을 끼워 넣어 새로운 문자열을 만드는 기능


        HttpRequest request = HttpRequest.newBuilder()
        // Http 요청 사항을 위한 빌더 객체 생성
                                .uri(URI.create("http://localhost:8080/api/login"))
                                // 요청을 보낼 주소 설정 ("/api/login")
                                .header("Content-Type", "application/json")
                                // 요청의 메타데이터(부가 정보)인 header 추가
                                // Content-Type : 보내는 메타데이터의 종류는
                                // application/json : json 형식이다
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                // 요청 방식을 Post로 지정, 요청에 담아 보낼 실제 데이터(Body) 설정
                                .build();
                                // 위의 정보들을 종합하여 변경 불가능한 최종 HttpRequest 객체 생성

        try{
            // 요청을 보내고 응답을 받음
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 1. client.send(...) : HttpClient 객체인 client가 .send() 메소드를 호출해서 요청을 전송
            // 2. request : HttpRequest 객체인 request의 요청 객체
            // 3. HttpResponse.BodyHandlers.ofString() : 서버로부터 받은 JSON 문자열을 String 타입으로 변환
            // 4. HttpResponse<String> response : 1번을 통해 서버로부터 받은 응답을 HttpResponse 객체로 반환

            // 서버의 응답 코드 확인
            if(response.statusCode() == 200){   // 200 = 성공을 의미
                System.out.println("로그인 성공");
            }
            else{
                System.out.println("로그인 실패" + response.body());
            }
        }
        catch(Exception e) {    // 예외 발생 시
            e.printStackTrace();    // 오류 발생 경로 출력
            System.out.println("서버 연결 오류");
        }

        System.out.println("로그인 진행");
        System.out.println("아이디: " + username);
        System.out.println("비밀번호: " + password);
    }

    //++ 회원가입 하이퍼링크 추가
    @FXML
    private void goToSignup(ActionEvent e) throws IOException {
        // signup.fxml 파일을 불러와 새로운 화면 구성
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getResource("/com/coin_investment/view/signup.fxml")
                )
        );

        // 현재 창(Stage)을 가져와 화면 전환
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

        // 새 Scene 설정 및 타이틀 변경
        stage.setScene(new Scene(root));
        stage.setTitle("회원가입");

        // 화면 표시
        stage.show();
    }

}
