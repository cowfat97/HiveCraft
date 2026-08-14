import sys
import os
current_path = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, current_path)
from edu_doc_loader import *
from edu_ppt_loader import *
from edu_img_loader import *
from edu_pdf_loader import *
