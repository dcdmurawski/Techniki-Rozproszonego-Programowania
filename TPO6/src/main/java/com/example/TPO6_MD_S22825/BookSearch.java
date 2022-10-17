package com.example.TPO6_MD_S22825;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "BookSearch", urlPatterns = "/BookSearch")
public class BookSearch extends HttpServlet {
    private final String url = "jdbc:mysql://localhost:3306/tpo6_mpo_s22825";
    private Connection con;
    private PrintWriter out;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        out = response.getWriter();
        String tytul = request.getParameter("tytul");
        String autor = request.getParameter("autor");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String user = "root";
            String pass = "";
            con = DriverManager.getConnection(url, user, pass);
            Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if(autor.isEmpty() && tytul.isEmpty()){
                s.execute("select * from book");
                printBooks(s.getResultSet());
            } else if(!autor.isEmpty() && !tytul.isEmpty()){
                s.execute("select * from book where bookname like '" + tytul + "' and author like '" + autor + "';");
                ResultSet rs = s.getResultSet();
                if(!rs.next())
                    out.println("Brak takiej ksiazki z takim autorem");
                else
                    printBooks(rs);
            } else if(autor.isEmpty()){
                s.execute("select * from book where bookname like '"+ tytul + "'");
                ResultSet rs = s.getResultSet();
                if(!rs.next())
                    out.println("Brak ksiazki z takim tytulem");
                else
                    printBooks(rs);
            } else {
                s.execute("select * from book where author like '"+ autor + "'");
                ResultSet rs = s.getResultSet();
                if(!rs.next())
                    out.println("Brak ksiazki z takim autorem");
                else
                    printBooks(rs);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void printBooks(ResultSet rs) throws SQLException{
        rs.beforeFirst();
        ArrayList<Book> books = new ArrayList<>();
        while(rs.next()){
            books.add(
                    new Book(
                            Integer.parseInt(rs.getString(1)),
                            rs.getString(2),
                            rs.getString(3),
                            Double.parseDouble(rs.getString(4)
                            )
                    )
            );
        }
        out.println("<html><body><table><tr><td>Lp</td><td>Nazwa Ksiazki</td><td>Autor</td><td>Cena</td>");
        for(Book book : books)
            out.println("<tr><td>"+book.getLp()+ "</td><td>" + book.getName() + "</td><td>" + book.getAuthor() + "</td><td>" + book.getPrice() + "</td>");
        out.println("</table></body></html>");
    }
}
