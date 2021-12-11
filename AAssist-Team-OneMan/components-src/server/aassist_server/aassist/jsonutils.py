import json

class Json:
    def encode(self,data):
         return (json.dumps(data,separators=(',', ':')))
    def decode(self,data):
        return json.loads(data);

#Usage :
# Json().encode(data) && Json().decode(data)

