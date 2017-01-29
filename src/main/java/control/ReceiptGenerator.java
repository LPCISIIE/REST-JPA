package control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import boundary.Account.AccountRepresentation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import entity.Account;
import entity.Sandwich;
import entity.Shipment;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

public class ReceiptGenerator {

    private static boolean folderCreated = false;
    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font normal = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
    private static Font small = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static Font tiny = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    private static Font info = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
    private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    public final static String FOLDER = "./orders/";

    /**
     * Main method : to create the PDF
     * @param order
     * @param uriInfo
     * @throws IOException
     */
    public static void create(Shipment order, UriInfo uriInfo, ServletContext servletContext) throws IOException{
        Boolean exists = new File(FOLDER).exists();

        if (exists) {
            folderCreated = true;
        }else{
            if (!folderCreated) {
                boolean done = (new File(FOLDER)).mkdirs();
                if (!done)
                    throw new IOException();
                else
                    folderCreated = true;
            }
        }

        if (folderCreated) {
            try {
                String file = FOLDER + order.getId() + ".pdf";
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                addMetaData(document, order);
                addTitlePage(document, order);
                addContent(document, order);
                addFooter(document,order,uriInfo);
                Paragraph logo = new Paragraph();
                addEmptyLine(logo, 2);
                document.add(logo);
                URL u = servletContext.getResource("/logo.jpg");
                Image img = Image.getInstance(u.getPath());
                img.setAlignment(Image.RIGHT);
                document.add(img);
                document.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to add Meta datas to the pdf (right click in adobe)
     * @param document
     * @param order
     */
    private static void addMetaData(Document document, Shipment order) {
        document.addTitle("Your order at Jean-bombeur.fr");
        document.addSubject("Receipt for the order " + order.getId());
        document.addKeywords("Order, JeanBombeur, JPA, Github, Java <3, insane");
        document.addAuthor("Xavier CHOPIN, Corentin LABROCHE, David LEBRUN, Alexis WURTH");
        document.addCreator("XC");
    }


    private static void addTitlePage(Document document, Shipment order) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("The " + new Date(),  catFont));
        addEmptyLine(preface, 2);
        preface.add(new Paragraph("Order : " + order.getId(), tiny));
        addEmptyLine(preface, 3);
        preface.add(new Paragraph("Dear " + order.getCustomer().getName() + ",",  small));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Here is the pdf of your receipt : ", small));
        addEmptyLine(preface, 2);

        document.add(preface);
    }

    /**
     * Method to add the order content
     * @param document
     * @param order
     * @throws DocumentException
     */
    private static void addContent(Document document, Shipment order) throws DocumentException {
        PdfPTable table = new PdfPTable(3);

        PdfPCell c1 = new PdfPCell(new Phrase("Product"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Description"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Price"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        for (Sandwich sandwich : order.getSandwiches()) {
            table.addCell(sandwich.getName());
            table.addCell(sandwich.getDescription());
            table.addCell((sandwich.getPrice() + " €"));
        }

        table.addCell(" ");
        table.addCell(" ");
        table.addCell("Total : " + order.getPrice() + " €");

        document.add(table);
    }

    /**
     * Method to add a footer in the pdf
     * @param document
     * @param order
     * @param uriInfo
     * @throws DocumentException
     */
    private static void addFooter(Document document, Shipment order, UriInfo uriInfo) throws DocumentException, IOException {
        Paragraph text = new Paragraph();
        addEmptyLine(text, 2);
        Account customer = order.getCustomer();

        if (customer.hasVIPCard())
            text.add(new Paragraph("You have " + customer.getVipCard() + " points on your card.", normal));
        else
            text.add(new Paragraph("You don't have a VIP card, get it for free there at : " + getUriForCardCreation(uriInfo), tiny));

        addEmptyLine(text, 2);
        text.add(new Paragraph("We also seize the opportunity, to thank you for your fidelity.", small));
        addEmptyLine(text, 1);

        Paragraph signature = new Paragraph("The Jean-Bombeur Team", info);
        signature.setAlignment(Element.ALIGN_RIGHT);
        text.add(signature);
        addEmptyLine(text, 1);

        try {

            URL u = ReceiptGenerator.class.getResource("WEB-INF/logo.jpg");
            Image img = Image.getInstance(u.getPath());
            img.setAlignment(Image.RIGHT);
            document.add(img);
        }catch (Exception e){
            e.printStackTrace();
        }

        document.add(text);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static String getUriForCardCreation(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountRepresentation.class)
                .path("create_card")
                .build()
                .toString();
    }
}