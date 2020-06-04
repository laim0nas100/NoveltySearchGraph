/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois;

import java.util.ArrayList;
import java.util.List;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.parsing.Lexer;
import lt.lb.commons.parsing.Literal;
import lt.lb.commons.parsing.Token;

/**
 *
 * @author Lemmin
 */
public class GraphReadingAPI {

    public static Orgraph readFullMatrix(List<String> lines, String seperator, int dimension) throws Lexer.StringNotTerminatedException {
        Lexer lex = new Lexer();
        lex.setSkipWhitespace(true);
        lex.addKeyword(seperator);
        lex.resetLines(lines);

        Orgraph graph = new Orgraph();

        Integer r = 0;
        Integer c = 0;
        while (true) {
            if (r == c) {
                c++;
                continue;
            }
            if (c >= dimension) {
                c = 0;
                r++;
                continue;
            }
            if (r >= dimension) {
                break;
            }
            Token get = lex.getNextToken().get();
            if (get instanceof Literal) {
                int parsed = Integer.parseInt(((Literal) get).value);
                graph.addLink(graph.newLink(r, c, parsed));
            } else {
                continue;
            }
            c++;
        }

        return graph;
    }
}
