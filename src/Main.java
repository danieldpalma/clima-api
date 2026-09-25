import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome da cidade: ");
        String cidade = scanner.nextLine();

        try {
            String dadosClimaticos = getDadosClimaticos(cidade);

            // 1006 => Localização não encontrada
            if(dadosClimaticos.contains("\"code\":1006")){
                System.out.println("Localização não encontrada. Por favor tente novamente.");
            } else {
                exibirDadosClimaticos(dadosClimaticos);
            }
        } catch (Exception e) {
            System.out.println("Erro ao obter dados climaticos: " + e.getMessage());
        }
    }

    public static String getDadosClimaticos(String cidade) throws Exception {
        final String API_URL = "http://api.weatherapi.com/v1/current.json?key=";
        Dotenv dotenv = Dotenv.load();
        String apikey = dotenv.get("API_KEY");

        String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);

        String apiUrl = API_URL + apikey + "&q=" + formataNomeCidade;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static void exibirDadosClimaticos(String dadosClimaticos) {
        // System.out.println("Dados originais (JSON) obtidos no site meteorológico: " + dadosClimaticos);

        JSONObject dadosJson = new JSONObject(dadosClimaticos);
        JSONObject infoMeteorologicas = dadosJson.getJSONObject("current");

        // Dados para localização
        String cidade = dadosJson.getJSONObject("location").getString("name");
        String pais = dadosJson.getJSONObject("location").getString("country");

        // Dados adicionais
        String condicaoTempo = infoMeteorologicas.getJSONObject("condition").getString("text");
        int umidade = infoMeteorologicas.getInt("humidity");
        float velocidadeVento = infoMeteorologicas.getFloat("wind_kph");
        float pressaoAtmosferica = infoMeteorologicas.getFloat("pressure_mb");
        float sensacaoTermica = infoMeteorologicas.getFloat("feelslike_c");
        float temperaturaAtual = infoMeteorologicas.getFloat("temp_c");

        // Data e hora retornada da API
        String dataHoraString = infoMeteorologicas.getString("last_updated");

        System.out.println("Informações meteorológicas para " + cidade + ", " + pais);
        System.out.println("Data e Hora: " + dataHoraString);
        System.out.println("Temperatura Atual: " + temperaturaAtual + "°C");
        System.out.println("Sensação térmica: " + sensacaoTermica + "°C");
        System.out.println("Condição do Tempo: " + condicaoTempo);
        System.out.println("Umidade: " + umidade + "%");
        System.out.println("Velocidade do Vento: " + velocidadeVento + " km/h");
        System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + " mb");
    }
}