package com.finance.analytics_service.infrastructure;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PdfHeaderFooterEvent extends PdfPageEventHelper {

    private final Long userId;

    public PdfHeaderFooterEvent(Long userId) {
        this.userId = userId;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte canvas = writer.getDirectContent();

        // 📌 CABEÇALHO
        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase("FINANCE ANALYTICS - RELATÓRIO"),
                297.5f,
                820,
                0
        );

        // 📌 RODAPÉ
        String footer = "Usuário: " + userId + " | Página " + writer.getPageNumber();

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(footer),
                297.5f,
                30,
                0
        );
    }
}
