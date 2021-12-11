import secrets
import hmac
import hashlib , time
import aassist.parallel as tpool
parapool = tpool.ParallelCompute()

def gen_token(data):
    return parapool.assignnew(hmac.new , (secrets.token_hex(16).encode('utf8'), data.encode('utf8'), hashlib.sha256,)).hexdigest()
