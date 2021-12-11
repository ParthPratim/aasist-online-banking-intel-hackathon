from multiprocessing.pool import ThreadPool

class ParallelCompute():
    parallel_workers = ThreadPool(4)
    def assignnew(self,func,args):

        return self.parallel_workers.apply_async(func,args=args)

    def assignnew_nowait(self,func,args=()):
        return self.parallel_workers.apply_async(func,args=args)

    def void_callback(self):
        return

# EDIRTED ROM 
