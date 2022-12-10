Select *
from documents;

Select *
from features
ORDER BY term;

select d.*, SUM(f.score) agScore
from features f, documents d
where f.term = ANY(string_to_array('bewerbung ander bietet bildet', ' '))
	AND f.docid = d.docid
GROUP BY d.docid
ORDER BY agScore desc
LIMIT 5;

WITH docsTerms as (
	select docid,  array_agg(term)::text[] as terms
	from features
	GROUP BY docid
)

select d.*, SUM(f.score) agScore
from features f, docsTerms dt, documents d
where dt.terms @> string_to_array('bewerbung ander', ' ')
	AND f.docid = dt.docid
GROUP BY d.docid
ORDER BY agScore
LIMIT 5;

WITH tfs as (
	select term, 1 + LOG(term_frequency) tf
	from features
), docfs as (
	select term, COUNT(DISTINCT docid) docs
	from features
	GROUP BY term
	ORDER BY term
), totalDocs as (
	select COUNT(DISTINCT docid)
	from documents
), idfs as (
	select f.term, LOG(ttds.count / df.docs) idf
	from features f, docfs df, totalDocs ttds
	WHERE f.term = df.term
)

update features
set tf = tfs.tf, idf = idfs.idf, score = tfs.tf * idfs.idf
FROM tfs, idfs
WHERE features.term = tfs.term
	AND features.term = idfs.term;

