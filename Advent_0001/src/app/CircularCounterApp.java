package app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CircularCounterApp {

    private static final int RANGE = 100; // 0..99
    private static final Pattern TOKEN_PATTERN = Pattern.compile("([RLrl])(\\d+)");

    public static void main(String[] args) throws IOException {

        int posicaoInicial = 50;
        String arquivoMovimentos = "movimentos.txt";
        String arquivoLog = "resultado_log.txt";

        int posicaoAtual = normalizar(posicaoInicial);
        int contadorZero = 0;

        String conteudo = Files.readString(Paths.get(arquivoMovimentos), StandardCharsets.UTF_8);

        try (BufferedWriter log = Files.newBufferedWriter(Path.of(arquivoLog), StandardCharsets.UTF_8)) {

            logLine(log, "POSIÇÃO INICIAL: " + format2(posicaoAtual));
            logLine(log, "================================");

            Matcher matcher = TOKEN_PATTERN.matcher(conteudo);

            while (matcher.find()) {

                char direcao = matcher.group(1).charAt(0);
                long valor = Long.parseLong(matcher.group(2));

                int antes = posicaoAtual;

                if (direcao == 'R' || direcao == 'r') {
                    posicaoAtual = normalizarLong(posicaoAtual + valor);
                } else {
                    posicaoAtual = normalizarLong(posicaoAtual - valor);
                }

                boolean zerou = posicaoAtual == 0;
                if (zerou) {
                    contadorZero++;
                }

                String linha = String.format("Movimento: %c%-6d | %02d -> %02d %s", Character.toUpperCase(direcao),
                        valor, antes, posicaoAtual, zerou ? "<-- ZERO" : "");

                logLine(log, linha);
                System.out.println(linha);
            }

            logLine(log, "================================");
            logLine(log, "PASSWORD (vezes que parou em 0): " + contadorZero);

            System.out.println("================================");
            System.out.println("PASSWORD: " + contadorZero);
            System.out.println("LOG: " + Path.of(arquivoLog).toAbsolutePath());
        }
    }

    private static int normalizar(int valor) {
        int r = valor % RANGE;
        if (r < 0)
            r += RANGE;
        return r;
    }

    private static int normalizarLong(long valor) {
        long r = valor % RANGE;
        if (r < 0)
            r += RANGE;
        return (int) r;
    }

    private static void logLine(BufferedWriter log, String linha) throws IOException {
        log.write(linha);
        log.newLine();
    }

    private static String format2(int v) {
        return String.format("%02d", v);
    }
}
