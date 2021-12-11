from cryptography.fernet import Fernet
import aassist.parallel as tpool

class P2Psecurity:
    parapool = tpool.ParallelCompute()
    def encrypt(self,raw_msg,key):
        cipher = self.parapool.assignnew(Fernet,(key,)).get()
        return self.parapool.assignnew(cipher.encrypt,(bytes(raw_msg,'ascii'),)).get()

    def decrypt(self,cipher_text,key):
        cipher = self.parapool.assignnew(Fernet,(key,)).get()
        return self.parapool.assignnew(cipher.decrypt,(cipher_text,)).get()

