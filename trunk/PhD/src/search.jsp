<%--
    Document   : index
    Created on : 09-Jan-2009, 22:33:30
    Author     : Bryan Duggan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="matt.*, java.util.*, abc.notation.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Search Results</title>
    </head>
    <body>

<%

        String root = getServletContext().getInitParameter("root");
        System.setProperty("user.dir", root);

        String q = request.getParameter("q");
        if ((q == null) || (q.length() == 0))
        {
            out.println("No query");
        }
        else
        {
            matt.ABCFinder finder = new matt.ABCFinder();
            finder.setSearchString(q);
            finder.setStartIn(MattProperties.getString("SearchCorpus"));
            finder.findFromIndex();
            PriorityQueue<ABCMatch> matches = finder.getPq();

            out.write("<table>");
            for (int i = 0 ; i < 10 ; i ++)
            {
                ABCMatch match = matches.poll();
                CorpusEntry corpusEntry = match.getCorpusEntry();
                out.write("<tr>");
                out.write("<td>" + match.getTitle() + "</td>");
                out.write("<td>" + corpusEntry.getFile() + "</td>");
                out.write("<td><a href=\"" + corpusEntry.getFile() + "\">ABC</a></td>");
                out.write("<td><a href=\"" + corpusEntry.getMidiFileName() + "\">MIDI</a></td>");
                out.write("</tr>");
            }
            out.write("</table>");
        }
        
%>


    </body>
</html>
