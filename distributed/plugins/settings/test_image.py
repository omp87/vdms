import vdms

props = {}
props["source"] = "youtube"
props["athletes"] = "serena williams"
props["entry_date"] = 20210804
props["class"] = "commercial"

addImage = {}
addImage["properties"] = props
addImage["format"] = "bin"
query = {}
query["AddImage"] = addImage

image_file = open("tmp.png", "rb")
image_blob = image_file.read()

db = vdms.vdms()
db.connect("localhost", 55550)

for a in range(100):
    a,b = db.query([query], [image_blob])
    print( a )
