import yaml,os

def map(code):
    bcode = code[:4]
    with open(os.getcwd()+"/aassist/config.yml","r") as config:
        cfg = yaml.safe_load(config)
        banks = cfg["banks"]
        for key,bank in banks.items():
            if bank["code"] == bcode:
                return key
    return -1

