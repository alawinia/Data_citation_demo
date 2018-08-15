create view V1 as select paper.paper_key, paper.title, paper.conference_key from paper where paper.paper_key=@x;
create view V2 as select proceedings.conference_key, proceedings.cname, proceedings.year, proceedings.pdf from proceedings where proceedings.conference_key=@x;
create view V3 as select paper.paper_key, paper.title, proceedings.cname, proceedings.year from paper JOIN proceedings ON paper.conference_key=proceedings.conference_key where proceedings.cname=@x and proceedings.year=@y;
create view V4 as select awards.awards_key, awards.title, awards.abstract, awards.begindate, awards.enddate, awards.amount from awards where awards.awards_key=@x;
create view V5 as select awards.awards_key, awards.title, awards.abstract, awards.begindate, awards.enddate, awards.amount from awards where awards.begindate=@x;