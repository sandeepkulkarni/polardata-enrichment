import traceback
import subprocess
import os
import json
import re


#Directory where data json files are present
rootdir='/Volumes/Blunder/polardata_json/application_pdf_train'
outdir='/Volumes/Blunder/polardata_json_grobid/application_pdf'

flag=False

#rootdir=sys.argv[1] #input dir
#outdir=sys.argv[2]   #output dir
def is_number(s):
    try:
        int(s)
        return True
    except ValueError:
        return False

#recursively read all files in the input directory
for root, subFolders, files in os.walk(rootdir):
    for fileName in files:
        if re.match(r'^\.', fileName):
            continue
        d={}

        with open(os.path.join(root,fileName), 'r+') as temp:
            d=json.load(temp)
            actualFileName=d["absoluteFilePath"]
            #get the path to the pdf file
            actualFileName=actualFileName.replace("F:\\","/Volumes/Blunder/")
            actualFileName=actualFileName.replace("\\","/")


            try:
                #send to tika server enabled with grobid
                result=os.popen("curl -T "+actualFileName+" -H 'Content-Disposition: attachment; filename="+actualFileName+"' http://localhost:9998/rmeta").read()
                parsed_json = json.loads(result)

                grobid_tags={}
                for k in parsed_json[0]:
                    if (k.find("grobid")!=-1):
                        grobid_tags[k]=parsed_json[0][k]

                d["grobid_tags"]=grobid_tags

                #if grobid identifed authors, send to scholar.py
                if("grobid:header_Authors" in grobid_tags):
                    authors=grobid_tags["grobid:header_Authors"].split(",")
                    for author in authors:
                        authorName = author.split(" ")

                        scholarAuthors=[]
                        authorNumber=0
                        for authorNamePart in authorName:
                            if is_number(authorNamePart):
                                authorNumber+=1
                            else:
                                try:
                                    t=scholarAuthors[authorNumber]
                                except:
                                    scholarAuthors.append("")
                                scholarAuthors[authorNumber]+=authorNamePart+" "
                        print(scholarAuthors)
                        scholaroutput={}
                        #add scholar info to the json
                        with open("output.json", "w+") as output:
                            for scholarAuthor in scholarAuthors:
                                command=["python3","scholar.py","-c","20","--author",scholarAuthor,"--json"]
                                subprocess.call(command, stdout=output);
                                output.seek(0,0)
                                content=output.readlines()
                                if len(content)==0:
                                    continue
                                count=0
                                finalcontent=[]
                                for line in content:
                                    if(count==0):
                                        line="["+line
                                    elif "}" in line:
                                        line=line+","
                                    finalcontent.append(line)
                                    count+=1
                                output.seek(0,0)
                                output.writelines(finalcontent[:-2])
                                output.write("}]")
                                output.seek(0,0)
                                scholaroutput=json.load(output)
                                d["scholar_tags"]=scholaroutput
                with open(os.path.join(outdir,fileName), 'w+') as temp2:
                    json.dump(d,temp2)
                    temp2.close()
            except:
                traceback.print_exc()
                continue

