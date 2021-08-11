import vdms

props = {}
props["frame"] = 156

props['abpqrstuv'] = 156
props["_creation"] = 100

addEntity = {}
addEntity["properties"] = props
addEntity["class"] = "vi"
query = {}
query["AddEntity"] = addEntity


db = vdms.vdms()
db.connect("localhost", 55550)

for a in range(100):
    a,b = db.query([query])
    print( a )
