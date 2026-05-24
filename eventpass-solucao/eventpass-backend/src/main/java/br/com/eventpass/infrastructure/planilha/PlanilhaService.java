package br.com.eventpass.infrastructure.planilha;

import br.com.eventpass.application.dto.request.ConvidadoRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlanilhaService {

    /**
     * Parseia Excel (.xlsx) ou CSV.
     * Colunas esperadas (case-insensitive):
     * nome | documento | telefone | email | grupo_tag | max_acompanhantes
     *
     * Apenas "nome" é obrigatório. As demais são opcionais.
     */
    public List<ConvidadoRequest> parsear(MultipartFile arquivo) {
        String nomeArquivo = arquivo.getOriginalFilename() != null
                ? arquivo.getOriginalFilename().toLowerCase() : "";

        try {
            if (nomeArquivo.endsWith(".xlsx") || nomeArquivo.endsWith(".xls")) {
                return parsearExcel(arquivo);
            } else if (nomeArquivo.endsWith(".csv")) {
                return parsearCsv(arquivo);
            } else {
                throw new IllegalArgumentException(
                    "Formato não suportado. Envie um arquivo .xlsx ou .csv");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao parsear planilha: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar a planilha: " + e.getMessage());
        }
    }

    // ── Excel ────────────────────────────────────────────────────────────────
    private List<ConvidadoRequest> parsearExcel(MultipartFile arquivo) throws Exception {
        List<ConvidadoRequest> resultado = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(arquivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row cabecalho = sheet.getRow(0);
            if (cabecalho == null) throw new IllegalArgumentException("Planilha sem cabeçalho.");

            int[] indices = resolverIndices(cabecalho);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || linhaVazia(row)) continue;

                String nome = valorCelula(row, indices[0]);
                if (nome == null || nome.isBlank()) continue; // pula linhas sem nome

                resultado.add(new ConvidadoRequest(
                        nome,
                        valorCelula(row, indices[1]),
                        valorCelula(row, indices[2]),
                        valorCelula(row, indices[3]),
                        valorCelula(row, indices[4]),
                        parseIntOuNull(valorCelula(row, indices[5]))
                ));
            }
        }
        return resultado;
    }

    // ── CSV ──────────────────────────────────────────────────────────────────
    private List<ConvidadoRequest> parsearCsv(MultipartFile arquivo) throws Exception {
        List<ConvidadoRequest> resultado = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {

            String cabecalhoLinha = reader.readLine();
            if (cabecalhoLinha == null) throw new IllegalArgumentException("CSV vazio.");

            String[] colunas = cabecalhoLinha.split("[;,]");
            int[] indices = resolverIndicesCsv(colunas);

            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] campos = linha.split("[;,]", -1);

                String nome = campoOuNull(campos, indices[0]);
                if (nome == null || nome.isBlank()) continue;

                resultado.add(new ConvidadoRequest(
                        nome,
                        campoOuNull(campos, indices[1]),
                        campoOuNull(campos, indices[2]),
                        campoOuNull(campos, indices[3]),
                        campoOuNull(campos, indices[4]),
                        parseIntOuNull(campoOuNull(campos, indices[5]))
                ));
            }
        }
        return resultado;
    }

    // ── Resolução de índices de colunas ──────────────────────────────────────
    private int[] resolverIndices(Row cabecalho) {
        int[] idx = {-1, -1, -1, -1, -1, -1};
        for (Cell cell : cabecalho) {
            String header = cell.getStringCellValue().toLowerCase().trim()
                    .replaceAll("[_\\-\\s]", "");
            int col = cell.getColumnIndex();
            switch (header) {
                case "nome"              -> idx[0] = col;
                case "documento","cpf","rg" -> idx[1] = col;
                case "telefone","whatsapp"  -> idx[2] = col;
                case "email"             -> idx[3] = col;
                case "grupo","grupotag","mesa","setor" -> idx[4] = col;
                case "acompanhantes","maxacompanhantes" -> idx[5] = col;
            }
        }
        if (idx[0] == -1) throw new IllegalArgumentException(
            "Coluna 'nome' não encontrada. Verifique o cabeçalho da planilha.");
        return idx;
    }

    private int[] resolverIndicesCsv(String[] colunas) {
        int[] idx = {-1, -1, -1, -1, -1, -1};
        for (int i = 0; i < colunas.length; i++) {
            String h = colunas[i].toLowerCase().trim().replaceAll("[_\\-\\s\"']", "");
            switch (h) {
                case "nome"              -> idx[0] = i;
                case "documento","cpf","rg" -> idx[1] = i;
                case "telefone","whatsapp"  -> idx[2] = i;
                case "email"             -> idx[3] = i;
                case "grupo","grupotag","mesa","setor" -> idx[4] = i;
                case "acompanhantes","maxacompanhantes" -> idx[5] = i;
            }
        }
        if (idx[0] == -1) throw new IllegalArgumentException(
            "Coluna 'nome' não encontrada no CSV.");
        return idx;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String valorCelula(Row row, int idx) {
        if (idx < 0) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default      -> null;
        };
    }

    private String campoOuNull(String[] campos, int idx) {
        if (idx < 0 || idx >= campos.length) return null;
        String v = campos[idx].trim().replaceAll("\"", "");
        return v.isBlank() ? null : v;
    }

    private Integer parseIntOuNull(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try { return Integer.parseInt(valor.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return null; }
    }

    private boolean linhaVazia(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}
