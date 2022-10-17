<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1250">
    <title>Book Searcher</title>
</head>
<body>
<center><h2>Podaj parametry książki (brak parametrów oznacza wypisanie wszystkich książek)</h2></center>
<hr>
<form method="get" action="BookSearch">
    Tytul<input type="text" size="50" name="tytul"><br>
    Autor<input type="text" size="30" name="autor"><br>
    <br><input type="submit" value="Wyślij formularz">
</form>
</body></html>