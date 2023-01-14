package DB;

import DB.Entities.DocumentEntity;
import DB.Entities.LinkEntity;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.Vectors;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import java.util.ArrayList;
import java.util.HashMap;

public class PageRank {
    private static final double DAMPING_FACTOR = 0.85;
    private static final double PROBABILITY_RANDOM_JUMP = 0.1;

    public DB db;

    HashMap<Integer, String> pages;
    HashMap<Integer, ArrayList<Integer>> links;

    HashMap<Integer, Integer> vectorPageMap;

    public PageRank(DB db){
        this.db = db;
    }

    public void compute(){
        // Retrieve pages and links from database
        pages = new HashMap<>();
        links = new HashMap<>();
        vectorPageMap = new HashMap<Integer, Integer>();

        ArrayList<DocumentEntity> eDocuments = db.getDocuments(db.selectTable("documents"));
        ArrayList<LinkEntity> eLinks = db.getLinks(db.selectTable("links"));

        for(int i = 0; i < eDocuments.size(); i++){
            DocumentEntity doc = eDocuments.get(i);
            pages.put(doc.getDocid(), doc.getUrl());
            vectorPageMap.put(i, doc.getDocid());
        }

        for(LinkEntity link : eLinks){
            if(links.get(link.getFrom_docid()) == null){
                links.put(link.getFrom_docid(), new ArrayList<>());
            }
            if(!links.get(link.getFrom_docid()).contains(link.getTo_docid())){
                links.get(link.getFrom_docid()).add(link.getTo_docid());
            }
        }

        // Create matrix and vector for PageRank computation
        int numPages = pages.size();
        Matrix transitionMatrix = new Basic2DMatrix(numPages, numPages);
        Vector pageRankVector = new BasicVector(numPages);
        for (int i = 0; i < numPages; i++) {
            DocumentEntity doc = eDocuments.get(i);

            // Set initial PageRank value for each page to 1/N
            pageRankVector.set(i, 1.0 / numPages);
            // Compute number of outgoing links for each page
            int numOutgoingLinks = 0;
            if(links.get(doc.getDocid()) != null){
                numOutgoingLinks = links.get(doc.getDocid()).size();
            }

            // Set transition probabilities in matrix
            for (int j = 0; j < numPages; j++) {
                DocumentEntity doc2 = eDocuments.get(j);

                if (numOutgoingLinks != 0 && links.get(doc.getDocid()).contains(doc2.getDocid())) {
                    // If there is a link from page i to page j, set transition probability
                    transitionMatrix.set(i, j, (1 - PROBABILITY_RANDOM_JUMP) / numOutgoingLinks);
                } else {
                    // If there is no link, set transition probability to a random jump
                    transitionMatrix.set(i, j, PROBABILITY_RANDOM_JUMP / numPages);
                }
            }
        }

        // Compute PageRank using power iteration method
        double residual = 1.0;
        /*the stopping criteria for the iterative PageRank computation is based on the difference between the PageRank vector in the current iteration and the PageRank vector in the previous iteration.
        The iteration continues until the error between the two vectors falls below a certain threshold (in this case, 1e-8).
        This stopping criteria is commonly used in practice because it allows the algorithm to achieve a good balance between accuracy and efficiency.
        The error threshold can be adjusted to trade off between these two factors, with a smaller threshold resulting in greater accuracy but longer runtime, and a larger threshold resulting in less accuracy but faster runtime.
        Another commonly used stopping criteria is the maximum number of iterations. This can be useful in cases where it is not known in advance how long the algorithm will take to converge, or when the error threshold is not sufficient to ensure the desired level of accuracy.*/

        while (residual > 1e-9) {
            // Save current PageRank vector
            Vector oldPageRankVector = pageRankVector;
            // Update PageRank vector
            pageRankVector = transitionMatrix.multiply(pageRankVector);
            pageRankVector = pageRankVector.multiply(DAMPING_FACTOR).add(oldPageRankVector.multiply(1 - DAMPING_FACTOR));
            // Compute residual as the sum of the absolute differences between the old and new PageRank vectors
            residual = pageRankVector.subtract(oldPageRankVector).fold(Vectors.mkManhattanNormAccumulator());
        }

        // Update database with computed PageRank values
        for (int i = 0; i < numPages; i++) {
            double pageRank = pageRankVector.get(i);
            Integer docid = vectorPageMap.get(i);
            db.updateEntityByKey("documents", "docid", docid, "pagerank", pageRank);
        }
    }
}
